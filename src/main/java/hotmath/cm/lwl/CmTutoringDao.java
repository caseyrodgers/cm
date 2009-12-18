package hotmath.cm.lwl;

import hotmath.cm.signup.HotmathSubscriberServiceTutoringSchoolStudent;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.lwl.LWLIntegrationManager;
import hotmath.lwl.LWLIntegrationManager.LwlAccountInfo;
import hotmath.lwl.LWLIntegrationManager.LwlSourceApplication;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberManager;
import hotmath.subscriber.HotMathSubscriberSignupInfo;
import hotmath.subscriber.PurchasePlan;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class CmTutoringDao {
    
    static Logger logger = Logger.getLogger(CmTutoringDao.class);
    
    /** Return information about this Student's Tutoring configuration
     * 
     * If a school student, then LWL info is tracked via the: HA_USER_LWL table.
     * This table uses the user_id as a key into the LWL_TUTORING. This is because
     * each student must be tracked/registered individually.
     * 
     * If a student does not have tutoring defined, we setup the minimal data
     * needed for this user to access LWL
     * 
     * NOTE: called from cm_lwl_launch.jsp
     * 
     * 
     * @param conn
     * @param uid 
     * @return
     * @throws Exception
     */
    public StudentTutoringInfo getStudentTutoringInfo(final Connection conn, Integer uid) throws Exception {
        
        
        logger.info("Getting tutor information for: " + uid);
        
        StudentModelI student = new CmStudentDao().getStudentModelBasic(conn, uid);
        Integer adminId = student.getAdminUid();
        AccountInfoModel accountInfo = new CmAdminDao().getAccountInfo(adminId);
        
        StudentTutoringInfo studentTutoringInfo = null;
        LwlAccountInfo studentLwlInfo= getUserLwlInfo(conn, uid); 
            
        /** Get both the admin and student LWL information.
         * 
         * 
         */
        LWLIntegrationManager.LwlAccountInfo adminLwlInfo = null;
        try {
            adminLwlInfo = LWLIntegrationManager.getInstance().getLwlIntegrationKey(accountInfo.getSubscriberId());
            studentTutoringInfo = new StudentTutoringInfo(accountInfo.getSubscriberId(), studentLwlInfo.getStudentId(), adminLwlInfo.getSchoolId(),adminLwlInfo.getAccountType());
        }
        catch(Exception e) {
            studentTutoringInfo = new StudentTutoringInfo(accountInfo.getSubscriberId(), studentLwlInfo.getStudentId(), 0,0);
        }
        
        
        
        // if a school account, then read the schoolNumber associated with the school
        HotMathSubscriber sub = HotMathSubscriberManager.findSubscriber(studentTutoringInfo.getSubscriberId());
        if(sub.getSubscriberType().equals("ST")) {
            AccountInfoModel school = new CmAdminDao().getAccountInfo(student.getAdminUid());
            
            /** If this school does not have tutoring enabled, then throw exception  
             * 
             */
            if(school.getHasTutoring() != null && !school.getHasTutoring().equals("Enabled") ) {
                throw new CmException("School '" + school.getSchoolName() + "' does not have tutoring enabled");
            }
            Integer schoolNumber = adminLwlInfo.getSchoolId();
            
            if(schoolNumber == 0) {
                throw new CmException("LWL student account error: No school_number found for '" + uid + "'. " +
                                      " Could be the LWL_TUTORING record associated with the subscriber does not exist.");
            }
            studentTutoringInfo.setSchoolNumber(schoolNumber);
        }
        
        if(studentTutoringInfo.getStudentNumber() == 0) {
            /** Setup basic info needed for LWL integration
             * 
             * @TODO: single subscriber user
             * 
             */
            HotMathSubscriber sub2 = HotMathSubscriberManager.findSubscriber(studentTutoringInfo.getSubscriberId());
            PurchasePlan plan = new PurchasePlan("TYPE_SERVICE_TUTORING_0");
            
            HotMathSubscriberSignupInfo signupInfo = HotMathSubscriberSignupInfo.createInfoForTestCard();
            signupInfo.setFirstName(student.getName());
            signupInfo.setLastName("");
            signupInfo.setStudentName(student.getName());
            signupInfo.setBillingZip(sub2.getZip());
            
            sub.setSignupInfo(signupInfo);
            List<PurchasePlan> plans = new ArrayList<PurchasePlan>();
            plans.add(plan);
            sub.purchaseServices(plans);
        }
        
        return studentTutoringInfo;
    }
    
    
    
    
    /** Return LWL info by named uid, attempt to create registration
     *  if no current info found.
     *  
     * @param conn
     * @param uid
     * @return
     * @throws Exception
     */
    public LwlAccountInfo getUserLwlInfo(final Connection conn, Integer uid) throws Exception {
        LwlAccountInfo lwlInfo = null;
        try {
            lwlInfo = LWLIntegrationManager.getInstance().getLwlIntegrationKey(uid.toString());
        }
        catch(Exception e) {
            /** If no registration information for user, then create one
             * 
             * @TODO: catch specific exception.
             */
            lwlInfo = registerStudentWithLwl(conn, uid);
        }
        
        return lwlInfo;
    }
    
    
    /** Registered user with uid as LWL user
     * 
     *  Use uid as the subcriber_id as the key into 
     *  LWL_TUTORING.  
     *  
     *  @TODO: do not use uid as the subscriber_id!
     *  
     * @param conn
     * @param uid
     * @return
     * @throws Exception
     */
    public LwlAccountInfo registerStudentWithLwl(final Connection conn, Integer uid) throws Exception {
       
        /** Setup a subscriber with the subscriber_id
         *  set to the user's UID.
         */
        HotMathSubscriber sub = new HotMathSubscriber();
        sub.setId(uid.toString());

        StudentModelI sm = new CmStudentDao().getStudentModelBasic(conn, uid);
        
        
        /** Setup a default LWL account to act as placeholder
         *  within LWL's database.
         */
        HotMathSubscriberSignupInfo info = HotMathSubscriberSignupInfo.createInfoForTestCard();
        info.setFirstName(sm.getName());
        info.setLastName("");
        
        

        
        /** What email should be used?
         * 
         */
        sub.setEmail("cm_student_" + uid + "@hotmath.com");
        sub.setResponsibleName(sm.getName());
        sub.setSignupInfo(info);
        
        /** Create a new Tutoring service add add to temp subscriber record
         * 
         */
        HotmathSubscriberServiceTutoringSchoolStudent service = new HotmathSubscriberServiceTutoringSchoolStudent();
        service.installService(sub, null);
        
        LwlAccountInfo acinfo = LWLIntegrationManager.getInstance().getLwlIntegrationKey(uid.toString());
        LWLIntegrationManager.getInstance().registerUserWithLwl(sub,acinfo, LwlSourceApplication.CATCHUP);
        
        return acinfo; 
    }
    
    
    
    /** Add basic tutoring capabilities for this user
     * 
     * @param uid
     * @throws Exception
     */
    public void addTutoring(final Connection conn, Integer uid) throws Exception {
        
        StudentModelI student = new CmStudentDao().getStudentModelBasic(conn, uid);
        Integer adminId = student.getAdminUid();
        AccountInfoModel accountInfo = new CmAdminDao().getAccountInfo(adminId);
        String subId = accountInfo.getSubscriberId();
        
        
        StudentTutoringInfo sti = getStudentTutoringInfo(conn, uid);
        
        logger.debug("Read StudentTutoringInfo for " + uid + "': " + sti);
    }
}

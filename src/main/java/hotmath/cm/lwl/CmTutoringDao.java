package hotmath.cm.lwl;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.lwl.LWLIntegrationManager;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberManager;
import hotmath.subscriber.HotMathSubscriberSignupInfo;
import hotmath.subscriber.PurchasePlan;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class CmTutoringDao {
    
    /** Return information about this Student's Tutoring configuration
     * 
     * If user does not have tutoring defined, we setup the minimal data
     * needed for this user to access LWL
     * 
     * @param conn
     * @param uid 
     * @return
     * @throws Exception
     */
    public StudentTutoringInfo getStudentTutoringInfo(final Connection conn, Integer uid) throws Exception {
        
        StudentModelI student = new CmStudentDao().getStudentModelBasic(conn, uid);
        Integer adminId = student.getAdminUid();
        AccountInfoModel accountInfo = new CmAdminDao().getAccountInfo(adminId);
        
        StudentTutoringInfo sti = new StudentTutoringInfo(accountInfo.getSubscriberId());
        
        try {
            LWLIntegrationManager.LwlAccountInfo lwlInfo = LWLIntegrationManager.getInstance().getLwlIntegrationKey(accountInfo.getSubscriberId());
            sti.setLwlId(lwlInfo.getStudentId());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        if(sti.getLwlId() == 0) {
            /** Setup basic info needed for LWL integration
             * 
             */
            HotMathSubscriber sub = HotMathSubscriberManager.findSubscriber(sti.getSubscriberId());
            PurchasePlan plan = new PurchasePlan("TYPE_SERVICE_TUTORING_0");
            
            HotMathSubscriberSignupInfo signupInfo = new HotMathSubscriberSignupInfo();
            signupInfo.setFirstName(student.getName());
            signupInfo.setLastName("");
            signupInfo.setStudentName(student.getName());
            signupInfo.setBillingZip(sub.getZip());
            
            // setup dummy LWL record
            signupInfo.setCardNumber("4012888888881881");
            signupInfo.setCardCcv("999");
            signupInfo.setCardExpMonth("12");
            signupInfo.setCardExpYear("10");
            signupInfo.setCardType("Visa");
            signupInfo.setBillingAddress("123 Street");
            signupInfo.setBillingCity("Atascadero");
            signupInfo.setBillingState("Ca");
            signupInfo.setCardEmail("APIPayPalPurchase@InstantMathHelp.com");
            signupInfo.setBillingZip("93422");
            signupInfo.setAutoCharge(false);
            
            sub.setSignupInfo(signupInfo);
            List<PurchasePlan> plans = new ArrayList<PurchasePlan>();
            plans.add(plan);
            sub.purchaseServices(plans);
        }
        
        return sti;
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
    }
}

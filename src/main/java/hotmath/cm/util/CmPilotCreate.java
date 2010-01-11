package hotmath.cm.util;

import hotmath.cm.signup.HotmathSubscriberServiceCatchup;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberManager;
import hotmath.subscriber.PurchasePlan;
import hotmath.subscriber.PurchasePlanDef;
import hotmath.subscriber.service.HotMathSubscriberServiceFactory;
import hotmath.subscriber.service.HotMathSubscriberServiceTutoring;
import hotmath.testset.ha.CmProgram;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** 
 * create user John Doe / jd12345

  Create these additional groups:
    - quizme - a selfreg group with show work required and auto-enroll prog
    - prealgebra - a selfreg group with show work req and Prealgebra prof prog
    - algebra1 - a selfreg group with show work req and algebra1 prof prog
    - geometry - a selfreg group with show work req and geometry prof prog
    - algebra2 - a selfreg group with show work req and algebra2 prof prog
    - gradprep - a self-reg group with show work req and cashee program
    
    
    Make sure there is a Catchup service
    
*/
public class CmPilotCreate {
    
    List<String> messages = new ArrayList<String>();
    Integer aid;
    public CmPilotCreate(String subscriberId, Boolean tutoringEnabled, Integer tutoringHours, Boolean showWorkRequired,Integer maxStudentCount) throws Exception {
        
        HotMathSubscriber sub = HotMathSubscriberManager.findSubscriber(subscriberId);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = HMConnectionPool.getConnection();
            
            String password = sub.getPassword();

            
            HotMathSubscriber subscriber = HotMathSubscriberManager.findSubscriber(subscriberId);

            subscriber.addService(HotMathSubscriberServiceFactory.create("catchup"),new PurchasePlan("TYPE_SERVICE_CATCHUP_MONTH"));
            
            /** 
             * add new HA_ADMIN account
             */
            String sql = "insert into HA_ADMIN(subscriber_id, passcode, user_name, create_date)values(?,?,?,now())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,subscriberId);
            pstmt.setString(2,"admin123");
            pstmt.setString(3,password);  // use subs password as username
            
            try {
                pstmt.executeUpdate();
                
                ResultSet rs = pstmt.getGeneratedKeys();
                rs.first();
                aid = rs.getInt(1);
                pstmt.close();

            }
            catch(Exception e) {
                ResultSet rs = conn.createStatement().executeQuery("select aid from HA_ADMIN where subscriber_id = '" + subscriberId + "'" );
                if(!rs.first())
                    throw new Exception("Could not find existing HA_ADMIN record for '" + subscriberId + "'");
                
                aid = rs.getInt(1);
                messages.add("New HA_ADMIN record not created, using existing.");
            }
            
            /** add new HA_USER attached to 
             * 
             */
            addJohnDoeUser(conn, aid,"John Doe","jd12345");
            
            /** setup default groups for user
             * 
             */
            setupPilotGroups(conn, aid);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null,pstmt,conn);
        }
    }
    
    private void setupPilotGroups(final Connection conn,Integer aid) throws Exception {
        
        // - quizme - a selfreg group with show work required and auto-enroll prog
        new CmAdminDao().createSelfRegistrationGroup(conn, aid, "quizme", CmProgram.AUTO_ENROLL, false, true);
        
        // - prealgebra - a selfreg group with show work req and Prealgebra prof prog
        new CmAdminDao().createSelfRegistrationGroup(conn, aid, "prealgebra", CmProgram.PREALG_PROF, false, true);
        
        //- algebra1 - a selfreg group with show work req and algebra1 prof prog
        new CmAdminDao().createSelfRegistrationGroup(conn, aid, "algebra1", CmProgram.ALG1_PROF, false, true);
        
        //- geometry - a selfreg group with show work req and geometry prof prog
        new CmAdminDao().createSelfRegistrationGroup(conn, aid, "geometry", CmProgram.GEOM_PROF, false, true);
        
        //- algebra2 - a selfreg group with show work req and algebra2 prof prog
        new CmAdminDao().createSelfRegistrationGroup(conn, aid, "algebra2", CmProgram.ALG2_PROF, false, true);
        
        //- gradprep - a self-reg group with show work req and cashee program
        new CmAdminDao().createSelfRegistrationGroup(conn, aid, "gradprep", CmProgram.CAHSEEHM, false, true);
    }
    
    public List<String> getMessages() {
        return messages;
    }


    public void setMessages(List<String> messages) {
        this.messages = messages;
    }


    public Integer getAid() {
        return aid;
    }


    public void setAid(Integer aid) {
        this.aid = aid;
    }


    private StudentModelI addJohnDoeUser(final Connection conn, Integer aid, String name, String password) throws Exception {
        
        CmStudentDao cmDao = new CmStudentDao();
        
        StudentModelI sm = checkIfJohnDoeExists(conn, aid, password);
        if(sm != null)
            return sm;

        /** create new */
        StudentModel student = new StudentModel();
        student.setName(name);
        student.setPasscode("student");
        student.setAdminUid(aid);
        student.setGroupId("1");
        student.setProgId("Prof");
        student.setSubjId("Pre-Alg");
        student.setPassPercent("70%");
        student.setTutoringAvail(false);
        student.setShowWorkRequired(false);
        student.setIsDemoUser(false);

        cmDao.addStudent(conn, student);
        
        return student;
    }
    
    
    /** Return the matching user, or null
     * 
     * @param conn
     * @param aid
     * @param password
     * @return
     * @throws Exception
     */
    private StudentModelI checkIfJohnDoeExists(final Connection conn, Integer aid,String password) throws Exception {
        List<StudentModelI> students = new CmStudentDao().getStudentModelByPassword(conn, aid, password);
        for(StudentModelI s: students) {
            if(s.getPasscode().equals(password))
                return s;
        }
        return null;
    }
}
    
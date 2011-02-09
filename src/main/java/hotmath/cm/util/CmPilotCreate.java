package hotmath.cm.util;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.model.CmPartner;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberManager;
import hotmath.subscriber.PurchasePlan;
import hotmath.subscriber.SalesZone;
import hotmath.subscriber.SalesZone.Person;
import hotmath.subscriber.id.IdCreateStategyImpHmPilot;
import hotmath.subscriber.service.HotMathSubscriberServiceFactory;
import hotmath.testset.ha.CmProgram;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import sb.mail.SbMailManager;

/**
 * create user John Doe / jd12345
 * 
 * Create these additional groups: - quizme - a selfreg group with show work
 * required and auto-enroll prog - prealgebra - a selfreg group with show work
 * req and Prealgebra prof prog - algebra1 - a selfreg group with show work req
 * and algebra1 prof prog - geometry - a selfreg group with show work req and
 * geometry prof prog - algebra2 - a selfreg group with show work req and
 * algebra2 prof prog - gradprep - a self-reg group with show work req and
 * cashee program - gradprep - a self-reg group with show work req and cashee
 * program - gradprepTX - a self-reg group with show work req and our TAKS
 * (Texas grad prep) program
 * 
 * 
 * Make sure there is a Catchup service
 */
public class CmPilotCreate {

	private static Logger logger = Logger.getLogger(CmPilotCreate.class);
	
	private static String NEW_LINE = System.getProperty("line.separator");

    List<String> messages = new ArrayList<String>();
    Integer aid;
    
    public CmPilotCreate() {
    }

    public CmPilotCreate(String subscriberId, Boolean tutoringEnabled, Integer tutoringHours, Boolean showWorkRequired,Integer maxStudentCount, CmPartner partner) throws Exception {

        HotMathSubscriber sub = HotMathSubscriberManager.findSubscriber(subscriberId);

        if (!sub.getSubscriberType().equals("ST"))
            throw new Exception("Subscriber account must be 'ST'");

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = HMConnectionPool.getConnection();

            String password = sub.getPassword();

            HotMathSubscriber subscriber = HotMathSubscriberManager.findSubscriber(subscriberId);
            if (subscriber.getService("catchup") == null)
                subscriber.addService(HotMathSubscriberServiceFactory.create("catchup"), new PurchasePlan("TYPE_SERVICE_CATCHUP_MONTH"));

            setMaxStudents(conn, subscriber, maxStudentCount);

            
            /** if admin already exists, then use its admin id
             * otherwise create new admin record.
             */
            boolean adminExists=false;
            PreparedStatement s1=null;
            try {
                s1 = conn.prepareStatement("select aid from HA_ADMIN where subscriber_id = ?");
                s1.setString(1, subscriberId);
                ResultSet rs = s1.executeQuery();
                if(rs.first()) {
                    adminExists = true;
                    aid = rs.getInt("aid");
                }
            }
            finally {
                SqlUtilities.releaseResources(null, s1, null);
            }
            
            
            if(!adminExists) {
                /**
                 * add new HA_ADMIN account
                 */
                String sql = "insert into HA_ADMIN(subscriber_id, passcode, user_name,partner_key, create_date)values(?,?,?,?,now())";
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, subscriberId);
                pstmt.setString(2, "admin123");
                pstmt.setString(3, password); // use subs password as username
                pstmt.setString(4, partner!=null?partner.key:null);
    
                try {
                    pstmt.executeUpdate();
    
                    ResultSet rs = pstmt.getGeneratedKeys();
                    rs.first();
                    aid = rs.getInt(1);
                    pstmt.close();
    
                } catch (Exception e) {
                    ResultSet rs = conn.createStatement().executeQuery(
                            "select aid from HA_ADMIN where subscriber_id = '" + subscriberId + "'");
                    if (!rs.first())
                        throw new Exception("Could not find existing HA_ADMIN record for '" + subscriberId + "'");
    
                    aid = rs.getInt(1);
                    messages.add("New HA_ADMIN record not created, using existing.");
                }
            }

            /**
             * setup default groups for user
             * 
             */
            setupPilotGroups(conn, aid);
            
            /**
             * add new HA_USER attached to
             * 
             */
            try {
                addJohnDoeUser(conn, aid, "John Doe", "jd12345", showWorkRequired, tutoringEnabled, tutoringHours);
            }
            catch(Exception e) {
                // fail silent .. might already exist.
            }

        } catch (Exception e) {
        	logger.error(String.format("*** problem creating pilot for subscriberID: %s", subscriberId), e);
        } finally {
            SqlUtilities.releaseResources(null, pstmt, conn);
        }
    }

    private void setupPilotGroups(final Connection conn, Integer aid) throws Exception {
    	
    	CmAdminDao dao = new CmAdminDao();

        // - quizme - a self-reg group for auto-enroll program
        dao.createSelfRegistrationGroup(conn, aid, "quizme", CmProgram.AUTO_ENROLL, false, false);

        // - prealgebra - a self-reg group for prealgebra prof prog
        dao.createSelfRegistrationGroup(conn, aid, "prealgebra", CmProgram.PREALG_PROF, false, false);

        // - algebra1 - a self-reg group for algebra1 prof program
        dao.createSelfRegistrationGroup(conn, aid, "algebra1", CmProgram.ALG1_PROF, false, false);

        // - geometry - a self-reg group for geometry prof program
        dao.createSelfRegistrationGroup(conn, aid, "geometry", CmProgram.GEOM_PROF, false, false);

        // - algebra2 - a self-reg group for algebra2 prof program
        dao.createSelfRegistrationGroup(conn, aid, "algebra2", CmProgram.ALG2_PROF, false, false);

        // - gradprep - a self-reg group for CAHSEE (CA grad prep) program
        dao.createSelfRegistrationGroup(conn, aid, "gradprep", CmProgram.CAHSEEHM, false, false);

        // - cahsee - a self-reg group for CAHSEE (CA grad prep) program
        dao.createSelfRegistrationGroup(conn, aid, "cahsee", CmProgram.CAHSEEHM, false, false);        

        // - gradprepTX - a self-reg group for TAKS (Texas grad prep) program
        dao.createSelfRegistrationGroup(conn, aid, "gradprepTX", CmProgram.TAKS, false, false);
        
        // - essentials - a self-reg group for essentials prof program
        dao.createSelfRegistrationGroup(conn, aid, "essentials", CmProgram.ESSENTIALS, false, false);

        // - exitexam - a self-ref group for nationals program
        dao.createSelfRegistrationGroup(conn, aid, "exitexam", CmProgram.NATIONAL, false, false);
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

    private StudentModelI addJohnDoeUser(final Connection conn, Integer aid, String name, String password,
            boolean showWorkEnabled, boolean tutoringEnabled, int tutoringHours) throws Exception {

        CmStudentDao cmDao = new CmStudentDao();

        StudentModelI sm = checkIfJohnDoeExists(conn, aid, password);
        if (sm != null)
            return sm;

        /** create new */
        StudentModel student = new StudentModel();
        student.setName(name);
        student.setPasscode(password);
        student.setAdminUid(aid);
        student.setGroupId("1");
        
        student.getProgram().setProgramType("Prof");
        student.getProgram().setSubjectId("Pre-Alg");
        student.setPassPercent("70%");
        student.getSettings().setTutoringAvailable(tutoringEnabled);
        student.getSettings().setShowWorkRequired(showWorkEnabled);
        student.setIsDemoUser(false);

        cmDao.addStudent(conn, student);

        return student;
    }

    /**
     * Return the matching user, or null
     * 
     * @param conn
     * @param aid
     * @param password
     * @return
     * @throws Exception
     */
    private StudentModelI checkIfJohnDoeExists(final Connection conn, Integer aid, String password) throws Exception {
        List<StudentModelI> students = new CmStudentDao().getStudentModelByPassword(conn, aid, password);
        for (StudentModelI s : students) {
            if (s.getPasscode().equals(password))
                return s;
        }
        return null;
    }

    /**
     * Set the max students for this subscriber
     * 
     * @param sub
     * @throws Exception
     */
    private void setMaxStudents(final Connection conn, HotMathSubscriber sub, int maxStudents) throws Exception {
        ResultSet rs = conn.createStatement().executeQuery(
                "select * from SUBSCRIBERS_SERVICES where service_name = 'catchup' and subscriber_id = '" + sub.getId()
                        + "'");
        if (!rs.first())
            throw new Exception("No 'catchup' service found");

        int ssid = rs.getInt("ssid");
        conn.createStatement().executeUpdate(
                "delete from SUBSCRIBERS_SERVICES_CONFIG_CATCHUP where subscriber_id = '" + sub.getId() + "'");

        String sql = "insert into SUBSCRIBERS_SERVICES_CONFIG_CATCHUP(subscriber_id, max_students, subscriber_svc_id)values(?,?,?)";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, sub.getId());
            ps.setInt(2, maxStudents);
            ps.setInt(3, ssid);
            int cnt = ps.executeUpdate();
            if (cnt != 1)
                throw new Exception("Could not set max_students for the catchup math service");
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    static DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    /**
     * Add a record to HA_ADMIN_PILOT_REQUEST table and send email to sales
     * manager
     * 
     */
    
    static public void test() throws Exception {
        HotMathSubscriber sub = HotMathSubscriberManager.createBasicAccount(null,null,null,null,null,null);        
    }
    
    
    static public Integer addPilotRequest(String title, String name, String school, String zip, String email,
            String phone, String userComments, String phoneWhen, String schoolPrefix,int studentCount) throws Exception {
        return addPilotRequest(title, name, school, zip, email, phone, userComments, phoneWhen, schoolPrefix, true,studentCount,null,null);
    }

    static public Integer addPilotRequest(String title, String name, String school, String zip, String email,
            String phone, String userComments, String phoneWhen, String schoolPrefix, boolean sendEmailConfirmation,int studentCount, CmPartner partner,String additionalEmails) throws Exception {
        
        
        Person salesPerson = SalesZone.getSalesPerson(zip);
        
        String ccEmails = null;
        if (additionalEmails != null) {
        	ccEmails = parseAdditionalEmails(additionalEmails);
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String sql = "insert into HA_ADMIN_PILOT_REQUEST(title,name,school,zip,email,phone,request_date,cc_emails)values(?,?,?,?,?,?,now(),?)";
            conn = HMConnectionPool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, name);
            ps.setString(3, school);
            ps.setString(4, zip);
            ps.setString(5, email);
            ps.setString(6, phone);
            ps.setString(7, ccEmails);

            ps.executeUpdate();

            // create a new Subscriber record based on this email
            String idToUse = HotMathSubscriber.createUniqueIDByStategy(new IdCreateStategyImpHmPilot(schoolPrefix));
            
            String ccEmailText;
            String comments;
            if (ccEmails != null && ccEmails.trim().length() > 0) {
                ccEmailText = ccEmails.trim();
                comments = String.format("%s Catchup Math online pilot request CM_pilot_HM (approx student count: %d) %scc_emails=%s%s",
                		_dateFormat.format(new Date()), studentCount, NEW_LINE, ccEmailText, NEW_LINE);
            }
            else {
            	ccEmailText = "NONE";
                comments = String.format("%s Catchup Math online pilot request CM_pilot_HM (approx student count: %d) %s",
                		_dateFormat.format(new Date()), studentCount, NEW_LINE);
            }

            HotMathSubscriber sub = HotMathSubscriberManager.createBasicAccount(idToUse, school, "ST", email, comments,new Date());
            sub.setResponsibleName(name);
            sub.setStatus("A");
            
            /** concatenate phone to end of zip */
            if(phone != null && phone.length() > 0)
                zip += " / " + phone;
            sub.setZip(zip);
            sub.setSalesZone(salesPerson.getLabel());
            sub.saveChanges();
            List<PurchasePlan> plans = new ArrayList<PurchasePlan>();
            plans.add(new PurchasePlan("TYPE_SERVICE_CATCHUP_PILOT"));
            sub.purchaseHotmath(null, plans, "", "", "", "", "", "", "", "", "", "", "", "");
            
            /** setup the CM pilot in CM_ADMIN
             * 
             */
            CmPilotCreate pilot = new CmPilotCreate(sub.getId(),false,0,false,1000,partner);
            
            
            if(sendEmailConfirmation) {
	            /** send tracking email to pilot requester
	             * 
	             */
	            try {
	                String emailTemplate = "CM Pilot " + salesPerson.getLabel();
	                sub.sendEmailConfirmation(emailTemplate, ccEmails);
	            }
	            catch(Exception e) {
	            	logger.error(String.format("*** problem creating pilot for school: %s", school), e);
	            }
	            
	            /** send tracking email to admin people
	             * 
	             */
	            String txt = "A request for a Catchup Math Pilot was created by:"
	                    + "\nSubscriber ID: " + idToUse
	                    + "\nTitle: " + title + "\nName: " + name + "\nSchool: " + school + "\nZip: " + zip
	                    + "\nEmail: " + email + "\nPhone: " + phone +  "\nPhone When: " + phoneWhen 
	                    + "\nStudent count: " + studentCount + "\nComments: " + userComments
	                    + "\nCC emails: " + ccEmailText
	                    + "\nsalesZone: " + salesPerson.getLabel();
	            try {
	                
	                /** send to sales rep and chuck */
	                String sendTo[] = {salesPerson.getEmail(),"cgrant.hotmath@gmail.com"};
	                SbMailManager.getInstance().
	                    sendMessage("Catchup Math Pilot Request", txt, sendTo,"registration@hotmath.com", "text/plain");
	            } catch (Exception e) {
	            	logger.error(String.format("*** problem sending pilot request email: %s", txt), e);
	            }
            }
            return pilot.getAid();
        } catch (Exception e) {
        	logger.error(String.format("*** problem adding pilot request for school: %s", school), e);
        } finally {
            SqlUtilities.releaseResources(null, ps, conn);
        }
        return -1;
    }

    static private String parseAdditionalEmails(String additionalEmails) {

    	if (additionalEmails != null && additionalEmails.trim().length() > 0) {

    		// split on new line
    		String[] emails = additionalEmails.split(System.getProperty("line.separator"));

    		StringBuilder sb = new StringBuilder();
    		boolean first = true;
    		for (String cce : emails) {
    			if (cce.trim().length() > 0) {
    				if (! first) sb.append(",");
    				sb.append(cce);
    				first = false;
    			}
    		}
    		return sb.toString();
    	}
    	return null;
    }

    static public void main(String as[]) {
    	/** make sure all active admin accounts have an essentials self-registration group
    	 * 
    	 */
    	Connection conn=null;
    	try {
    		conn = HMConnectionPool.getConnection();
    		String sql = "select aid from HA_ADMIN";
    		ResultSet rs = conn.createStatement().executeQuery(sql);
    		while(rs.next()) {
    			int aid = rs.getInt("aid");

    			try {
    				new CmAdminDao().createSelfRegistrationGroup(conn, aid,"essentials" , CmProgram.ESSENTIALS, false, false);
    			}
    			catch(Exception ex) {
    				logger.error("Error creating group", ex);
    			}
    		}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	finally {
    		SqlUtilities.releaseResources(null,null,conn);
    	}
    	System.exit(0);
    }
}

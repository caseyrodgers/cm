package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserInfo.AccountType;
import hotmath.gwt.cm_rpc.client.model.GroupInfoModel;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.CmUserException;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CreateAutoRegistrationAccountAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.testset.ha.ChapterInfo;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaUser;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/** Create Student accounts based on StudentModel (template) passed via UID assigned to Action
 *  and password (generated).
 *  
 *  Fail if a new password cannot be found
 **/
public class CreateAutoRegistrationAccountCommand implements ActionHandler<CreateAutoRegistrationAccountAction, RpcData> {


    static Logger __logger = Logger.getLogger(CreateAutoRegistrationAccountCommand.class);
    
    /** Return RpcData with fields:
     *
     * Catch any local exceptions and return simply error message.
     * 
     *  TODO: create specific object instead of RpcData. (note: new values in rpcdata does not break RPC ...)
     * 
     *    key = new login key
     *    error_message
     */
    public RpcData execute(Connection conn, CreateAutoRegistrationAccountAction action) throws Exception {
        try {
            return executeAux(conn, action);
        }
        catch(CmUserException cm) {
            __logger.warn("User exception handled", cm);
            RpcData rdata = new RpcData();
            rdata.putData("error_message", cm.getLocalizedMessage());
            return rdata;
        }
    }
    
    private RpcData executeAux(Connection conn, CreateAutoRegistrationAccountAction action) throws Exception {
        
        long millis = 0;

        CmStudentDao dao = CmStudentDao.getInstance();
        millis = System.currentTimeMillis();
        StudentModelI studentModel = dao.getStudentModelBase(conn, action.getUserId(), true);
    	if (__logger.isDebugEnabled())
        	__logger.debug("+++ CARAC getStudentModel took: " + (System.currentTimeMillis()-millis) + " msec");

    	String password;
    	if (action.isSelfPay() == true) {
    		// only get unique passwords for self-pay
    	    password = getUniquePassword(action.getPassword(), studentModel.getAdminUid(), conn);
    	}
    	else {
    		checkPassword(studentModel.getAdminUid(), action.getPassword(), conn);
			password = action.getPassword();
    	}

        studentModel.setPasscode(password);
        studentModel.setName(action.getUser());
        studentModel.setEmail(action.getEmail());
        studentModel.setSelfPay(action.isSelfPay());
        millis = System.currentTimeMillis();
        studentModel = dao.addStudent(conn, studentModel);

    	if (__logger.isDebugEnabled())
        	__logger.debug("+++ CARAC addStudent() took: " + (System.currentTimeMillis()-millis) + " msec");

        CmUserProgramDao upDao = CmUserProgramDao.getInstance();
        millis = System.currentTimeMillis();
        // TODO: make ProgramInfo an attribute of Student Model and skip this lookup
        StudentUserProgramModel si = upDao.loadProgramInfoCurrent(studentModel.getUid());
    	if (__logger.isDebugEnabled())
        	__logger.debug("+++ CARAC loadProgramInfoCurrent() took: " + (System.currentTimeMillis()-millis) + " msec");

    	// TODO: student was just created, shouldn't  all active info be 0
        millis = System.currentTimeMillis();
        StudentActiveInfo activeInfo = dao.loadActiveInfo(studentModel.getUid());
    	if (__logger.isDebugEnabled())
        	__logger.debug("+++ CARAC loadActiveInfo() took: " + (System.currentTimeMillis()-millis) + " msec");
                
        HaTestDefDao hdao = HaTestDefDao.getInstance();
        millis = System.currentTimeMillis();
        HaTestDef testDef = hdao.getTestDef(si.getTestDefId());
    	if (__logger.isDebugEnabled())
        	__logger.debug("+++ CARAC getTestDef() took: " + (System.currentTimeMillis()-millis) + " msec");
        
        millis = System.currentTimeMillis();
        ChapterInfo chapterInfo = hdao.getChapterInfo(conn, si);
    	if (__logger.isDebugEnabled())
        	__logger.debug("+++ CARAC getChapterInfo() took: " + (System.currentTimeMillis()-millis) + " msec");
        
        millis = System.currentTimeMillis();
        AccountType accountType = CmAdminDao.getInstance().getAccountType(conn,studentModel.getAdminUid());
    	if (__logger.isDebugEnabled())
        	__logger.debug("+++ CARAC getAccountType() took: " + (System.currentTimeMillis()-millis) + " msec");

        /** Create title sections.  The main title
         *  will contain the chapter # (if any).  The
         *  subtitle will contain the chapter title (if any)
         */
        String testTitle = testDef.getName();
        String subTitle=null;
        if(chapterInfo != null) {
            testTitle += ", #" + chapterInfo.getChapterNumber();
            subTitle = chapterInfo.getChapterTitle();
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUid(studentModel.getUid());

        //TODO: default all of these to 0?
        userInfo.setTestId(activeInfo.getActiveTestId());
        userInfo.setRunId(activeInfo.getActiveRunId());
        userInfo.setProgramSegment(activeInfo.getActiveSegment());
        userInfo.setSessionNumber(activeInfo.getActiveRunSession());

        userInfo.setUserName(studentModel.getName());
        userInfo.setBackgroundStyle(studentModel.getBackgroundStyle());
        userInfo.setProgramName(testTitle);
        userInfo.setSubTitle(subTitle);
        userInfo.setShowWorkRequired(studentModel.getSettings().getShowWorkRequired());
        userInfo.setTutoringAvail(studentModel.getSettings().getTutoringAvailable());

        userInfo.setUserAccountType(accountType);

        userInfo.setPassPercentRequired(si.getConfig().getPassPercent());
        userInfo.setProgramSegmentCount(testDef.getTotalSegmentCount());

        //TODO: shouldn't this be 0 and why use action.getUserId()?
        userInfo.setViewCount(dao.getTotalInmHViewCount(conn,action.getUserId()));

        // Make a new HA_USER_LOGIN entry and return key
        HaUser huser = new HaUser();
        huser.setUid(userInfo.getUid());
        huser.setLoginName(action.getUser());
        
        RpcData rdata = new RpcData();
        rdata.putData("key", HaLoginInfoDao.getInstance().addLoginInfo(conn, huser, new ClientEnvironment(),true));
        rdata.putData("uid", userInfo.getUid());
        
        AccountInfoModel admin = CmAdminDao.getInstance().getAccountInfo(studentModel.getAdminUid());
        
        rdata.putData("userName", admin.getAdminUserName());
        rdata.putData("loginName", admin.getSubscriberPassword());
        rdata.putData("password",  password);

        // get self-reg Group name
        GroupInfoModel groupMdl = CmAdminDao.getInstance().getGroupById(studentModel.getGroupId(), studentModel.getAdminUid());
        rdata.putData("groupName", groupMdl.getGroupName());

        return rdata;
    }

    private void checkPassword(int adminUid, String password, final Connection conn) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        long millis = 0;
        String sqlCheck = CmMultiLinePropertyReader.getInstance().getProperty("AUTO_CREATE_TEMPLATE_CHECK");
        try {
            stmt = conn.prepareStatement(sqlCheck);
            stmt.setInt(1, adminUid);
            stmt.setString(2, password);
            
            millis = System.currentTimeMillis();
            rs = stmt.executeQuery();
            if(rs.first()) {
            	//TODO: different message since password is generated not selected?
                __logger.error("self registration password '" + password + "' cannot match any existing self-registration template group names.");
                throw new CmUserException("Please choose a different password");
            }
        }
        finally {
        	if (__logger.isDebugEnabled())
            	__logger.debug("+++ CARAC check password took: " + (System.currentTimeMillis()-millis) + " msec");
            SqlUtilities.releaseResources(rs, stmt, null);
        }
		// TODO Auto-generated method stub
		
	}

	private String getUniquePassword(String password, int adminUid, final Connection conn) throws Exception {
        String sqlCheck = CmMultiLinePropertyReader.getInstance().getProperty("AUTO_CREATE_PASSWORD_CHECK");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long millis = System.currentTimeMillis();
        try {
            stmt = conn.prepareStatement(sqlCheck);
            stmt.setInt(1, adminUid);
            stmt.setString(2, password+"%");
            stmt.setInt(3, adminUid);
            stmt.setString(4, password+"%");
            rs = stmt.executeQuery();

            Set<Character> excludedChars = new HashSet<Character>();
            excludedChars.add('I');
            excludedChars.add('O');
            while(rs.next()) {
            	String existingPassword = rs.getString("password");

            	if (existingPassword.length() > password.length()+1) continue;

            	if (existingPassword.equalsIgnoreCase(password) == false) {
                	int length = existingPassword.length();
                	char lastChar = existingPassword.substring(length-1, length).charAt(0);
                	excludedChars.add(lastChar);
            	}
            }

            String foundChar = null;
            int limit = 100;
            while(limit-- > 0) {
            	char test = getRandomSuffix();
            	if (excludedChars.contains(test) == false) {
            		// found an unused char (A-Z)
            		foundChar = String.valueOf(test);
                    break;
            	}
            }

            if (foundChar != null) {
                return password + foundChar;	
            }
            else {
            	//TODO: a better message?
                __logger.error("self registration password '" + password + "' cannot match any existing self-registration template group names.");
                throw new CmUserException("Sorry, we encountered a problem while generating your password.  Please send an email to support@catchupmath.com for assistance.");
            }
        }
        finally {
        	if (__logger.isDebugEnabled())
            	__logger.debug("+++ CARAC check password took: " + (System.currentTimeMillis()-millis) + " msec");
            SqlUtilities.releaseResources(rs, stmt, null);
        }
    }

    private char getRandomSuffix() {
    	return (char)((int)(Math.random() * 25) + 65);
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateAutoRegistrationAccountAction.class;
    }
    
}

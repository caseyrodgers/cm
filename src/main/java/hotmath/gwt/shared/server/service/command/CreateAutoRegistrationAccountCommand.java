package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserInfo.AccountType;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountAction;
import hotmath.gwt.shared.client.util.CmUserException;
import hotmath.testset.ha.ChapterInfo;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaUser;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

/** Create Student accounts based on StudentModel (template) passed via UID assigned to Action
 *  and password (generated).
 *  
 *  Fail if password matches an existing password
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

        /** make sure that password does not match a group name 
         *  attached to a is_auto_create_template
         * 
         */
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        CmStudentDao dao = CmStudentDao.getInstance();
        millis = System.currentTimeMillis();
        StudentModelI studentModel = dao.getStudentModelBase(conn, action.getUserId(), true);
    	if (__logger.isDebugEnabled())
        	__logger.debug("+++ CARAC getStudentModel took: " + (System.currentTimeMillis()-millis) + " msec");

        String sqlCheck = CmMultiLinePropertyReader.getInstance().getProperty("AUTO_CREATE_TEMPLATE_CHECK");
        try {
            stmt = conn.prepareStatement(sqlCheck);
            stmt.setInt(1, studentModel.getAdminUid());
            stmt.setString(2, action.getPassword());
            
            millis = System.currentTimeMillis();
            rs = stmt.executeQuery();
            if(rs.first()) {
            	//TODO: different message since password is generated not selected?
                __logger.error("self registration password '" + action.getPassword() + "' cannot match any existing self-registration template group names.");
                throw new CmUserException("Please choose a different password");
            }
        }
        finally {
        	if (__logger.isDebugEnabled())
            	__logger.debug("+++ CARAC check password took: " + (System.currentTimeMillis()-millis) + " msec");
            SqlUtilities.releaseResources(rs, stmt, null);
        }

        studentModel.setPasscode(action.getPassword());
        studentModel.setName(action.getUser());
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
        rdata.putData("key", new HaLoginInfoDao().addLoginInfo(conn, huser, "auto_registration",true));
        return rdata;
    }
    
    
    //@Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateAutoRegistrationAccountAction.class;
    }
    
}

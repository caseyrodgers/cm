package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.CmUserException;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserInfo.AccountType;
import hotmath.gwt.cm_rpc.client.model.CmProgramAssign;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.ParallelProgramLoginAction;
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

/** Logs user into Parallel Program
 * 
 *  If user has not previously logged into this parallel program, then assign new program and preserve
 *  previous program if not already preserved (CM_PROGRASM, CM_PROGRAM_ASSIGN)
 *  
 *  If user has logged into this parallel program, but it is not their current Program, then re-assign Program
 *  (CM_PROGRAM_ASSIGN)
 *  
 *  If user's current Program assignment is this parallel program, no reset is needed.
 *  
 *  Fail if Admin ID of Parallel Program and Student do not match or Student password does not exist.
 **/

public class ParallelProgramLoginCommand implements ActionHandler<ParallelProgramLoginAction, RpcData> {

    static Logger __logger = Logger.getLogger(ParallelProgramLoginCommand.class);
    
    /** Return RpcData with fields:
     *
     * Catch any local exceptions and return simply error message.
     * 
     *  TODO: create specific object instead of RpcData. (note: new values in rpcdata does not break RPC ...)
     * 
     *    key = new login key
     *    error_message
     */
    public RpcData execute(Connection conn, ParallelProgramLoginAction action) throws Exception {
        try {
            return executeAux(conn, action);
        }
        catch(CmUserException ue) {
            __logger.warn("User exception handled", ue);
            RpcData rdata = new RpcData();
            rdata.putData("error_message", ue.getLocalizedMessage());
            return rdata;
        }
    }
    
    private RpcData executeAux(Connection conn, ParallelProgramLoginAction action) throws Exception {
        
    	
        long millis = 0;

        /** 
         * supplied password must be for Student that is assigned to Admin that 'owns' the Parallel Program
         * identified by "parallelProgId" 
         */
        ParallelProgramDao dao = ParallelProgramDao.getInstance();
        boolean isParallelProgramStudent = dao.isParallelProgramStudent(action.getParallelProgId(), action.getPassword());

        if (! isParallelProgramStudent) {
        	__logger.warn(String.format("Selected Parallel Program, ID: %d, is not available for password: %s",
        			action.getParallelProgId(), action.getPassword()));
            throw new CmUserException("Selected Parallel Program is not available for your password.");
        }

        /**
         * Check if selected parallel program is currently assigned to Student
         */
        boolean isAssigned = dao.isParallelProgramAssignedToStudent(action.getParallelProgId(), action.getPassword());
        __logger.debug("+++ isAssigned: " + isAssigned);

        if (isAssigned == false) {
        	// selected Parallel Program not currently assigned to Student
        	// determine if previously assigned
        	boolean prevAssigned = dao.parallelProgramPrevAssignedToStudent(action.getParallelProgId(), action.getPassword());

        	if (prevAssigned == false) {
        		// add assignment, and start as with any new Program using "CmStudentDao.assignProgramToStudent()"

        		
        		CmProgramAssign cmProgAssign = new CmProgramAssign();
        		
        		dao.addProgramAssignment(cmProgAssign);
        	}
        	
        	else {
        		// restore state from CM_PROGRAM_ASSIGN to HA_USER and CM_USER_PROGRAM and...
        	}
        	
        	
        	
        	
        	
        	
        }
        // return "key"
        
        /*
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
*/
        /** Create title sections.  The main title
         *  will contain the chapter # (if any).  The
         *  subtitle will contain the chapter title (if any)
          */
/*
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
*/
        RpcData rdata = new RpcData();
        rdata.putData("key", "1234567890");
        return rdata;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ParallelProgramLoginAction.class;
    }
    
}
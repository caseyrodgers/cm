package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.util.JsonUtil;
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
import hotmath.gwt.cm_rpc.client.model.CmProgram;
import hotmath.gwt.cm_tools.client.model.CustomProgramComposite;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.shared.client.rpc.action.ParallelProgramLoginAction;
import hotmath.testset.ha.ChapterInfo;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaUser;
import hotmath.testset.ha.StudentUserProgramModel;

import java.sql.Connection;

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

    static final Logger LOGGER = Logger.getLogger(ParallelProgramLoginCommand.class);
    
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
            LOGGER.warn("User exception handled", ue);
            RpcData rdata = new RpcData();
            rdata.putData("error_message", ue.getLocalizedMessage());
            return rdata;
        }
    }
    
    private RpcData executeAux(Connection conn, ParallelProgramLoginAction action) throws Exception {
        
    	CmStudentDao stuDao;
    	
        long millis = 0;
		int userId; 

        /** 
         * supplied password must be for Student that is assigned to Admin that 'owns' the Parallel Program
         * identified by "parallelProgId" 
         */
        ParallelProgramDao ppDao = ParallelProgramDao.getInstance();
        boolean isParallelProgramStudent = ppDao.isParallelProgramStudent(action.getParallelProgId(), action.getPassword());

        if (! isParallelProgramStudent) {
        	LOGGER.warn(String.format("Selected Parallel Program, ID: %d, is not available for password: %s",
        			action.getParallelProgId(), action.getPassword()));
            throw new CmUserException("Selected Parallel Program is not available for your password.");
        }

    	userId = ppDao.getStudentUserId(action.getParallelProgId(), action.getPassword());

    	stuDao = CmStudentDao.getInstance();
    	StudentModelI stuMdl = stuDao.getStudentModelBase(conn, userId);

        /**
         * Check if selected parallel program is currently assigned to Student
         */
        boolean isAssigned = ppDao.isParallelProgramAssignedToStudent(action.getParallelProgId(), userId);
        if (LOGGER.isDebugEnabled()) LOGGER.debug("+++ isAssigned: " + isAssigned);

        if (isAssigned == false) {
        	assignParallelProgram(conn, action.getParallelProgId(), userId, stuMdl);
        }

        CmUserProgramDao upDao = CmUserProgramDao.getInstance();
        millis = System.currentTimeMillis();
        // TODO: make ProgramInfo an attribute of Student Model and skip this lookup
        StudentUserProgramModel stuUserProgMdl = upDao.loadProgramInfoCurrent(userId);
    	if (LOGGER.isDebugEnabled())
        	LOGGER.debug("+++ PP loadProgramInfoCurrent() took: " + (System.currentTimeMillis()-millis) + " msec");

        millis = System.currentTimeMillis();
        StudentActiveInfo activeInfo = stuDao.loadActiveInfo(userId);
    	if (LOGGER.isDebugEnabled())
        	LOGGER.debug("+++ PP loadActiveInfo() took: " + (System.currentTimeMillis()-millis) + " msec");
                
        HaTestDefDao hdao = HaTestDefDao.getInstance();
        millis = System.currentTimeMillis();
        HaTestDef testDef = hdao.getTestDef(stuUserProgMdl.getTestDefId());
    	if (LOGGER.isDebugEnabled())
        	LOGGER.debug("+++ PP getTestDef() took: " + (System.currentTimeMillis()-millis) + " msec");
        
        millis = System.currentTimeMillis();
        ChapterInfo chapterInfo = hdao.getChapterInfo(conn, stuUserProgMdl);
    	if (LOGGER.isDebugEnabled())
        	LOGGER.debug("+++ PP getChapterInfo() took: " + (System.currentTimeMillis()-millis) + " msec");
        
        millis = System.currentTimeMillis();
        AccountType accountType = CmAdminDao.getInstance().getAccountType(conn, stuMdl.getAdminUid());
    	if (LOGGER.isDebugEnabled())
        	LOGGER.debug("+++ PP getAccountType() took: " + (System.currentTimeMillis()-millis) + " msec");

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
        userInfo.setUid(userId);

        userInfo.setTestId(activeInfo.getActiveTestId());
        userInfo.setRunId(activeInfo.getActiveRunId());
        userInfo.setProgramSegment(activeInfo.getActiveSegment());
        userInfo.setSessionNumber(activeInfo.getActiveRunSession());

        userInfo.setUserName(stuMdl.getName());
        userInfo.setBackgroundStyle(stuMdl.getBackgroundStyle());
        userInfo.setProgramName(testTitle);
        userInfo.setSubTitle(subTitle);
        userInfo.setShowWorkRequired(stuMdl.getSettings().getShowWorkRequired());
        userInfo.setTutoringAvail(stuMdl.getSettings().getTutoringAvailable());

        userInfo.setUserAccountType(accountType);

        userInfo.setPassPercentRequired(stuUserProgMdl.getConfig().getPassPercent());
        userInfo.setProgramSegmentCount(testDef.getTotalSegmentCount());

        userInfo.setViewCount(stuDao.getTotalInmHViewCount(conn, userId));

        // Make a new HA_USER_LOGIN entry and return key
        HaUser huser = new HaUser();
        huser.setUid(userInfo.getUid());
        huser.setLoginName(action.getUser());
        
        RpcData rdata = new RpcData();
        rdata.putData("key", HaLoginInfoDao.getInstance().addLoginInfo(conn, huser, new ClientEnvironment(), true));

        return rdata;
    }

	public void assignParallelProgram(final Connection conn,
			int parallelProgId, int userId,
			StudentModelI stuMdl) throws Exception {

		CmStudentDao stuDao = CmStudentDao.getInstance();
		ParallelProgramDao ppDao = ParallelProgramDao.getInstance();
		
		if (stuMdl == null) {
			stuMdl = stuDao.getStudentModelBase(conn, userId);
		}
		
		// if current Program not in CM_PROGRAM or CM_PROGRAM_ASSIGN add it
		boolean progExists = ppDao.currentProgramExistsForStudent(userId);
		if (LOGGER.isDebugEnabled()) LOGGER.debug("+++ progExists: " + progExists);
		
		boolean progAssignmentExists = false;
		CmProgram cmProg;
		if (progExists == false) {
			// current Program not in CM_PROGRAM
			cmProg = ppDao.addCurrentProgramForStudent(userId);
		}
		else {
			cmProg = ppDao.getCmProgramForUserId(userId);
			progAssignmentExists = ppDao.programAssignmentExistsForStudent(userId);
		}
		
		if (progAssignmentExists == false) {
			// add CM Program Assignment for Student
			StudentActiveInfo stuActiveInfo = stuDao.loadActiveInfo(userId);
			CmProgramAssign cmProgAssign = new CmProgramAssign();
			cmProgAssign.setCmProgram(cmProg);
			cmProgAssign.setUserId(userId);
			cmProgAssign.setUserProgId(stuMdl.getProgram().getProgramId());
			cmProgAssign.setProgSegment(stuActiveInfo.getActiveSegment());
			cmProgAssign.setRunId(stuActiveInfo.getActiveRunId());
			cmProgAssign.setRunSession(stuActiveInfo.getActiveRunSession());
			cmProgAssign.setSegmentSlot(stuActiveInfo.getActiveSegmentSlot());
			ppDao.addProgramAssignment(cmProgAssign);            	
		}
		else {
			//nothing to do
		}

		// selected Parallel Program not currently assigned to Student
		// determine if previously assigned
		boolean prevAssigned = ppDao.parallelProgramPrevAssignedToStudent(parallelProgId, userId);
		if (LOGGER.isDebugEnabled()) LOGGER.debug("+++ prevAssigned: " + prevAssigned);
		
		if (prevAssigned == false) {
			// add assignment, and start as with any new Program using "CmStudentDao.assignProgramToStudent()"
			cmProg = ppDao.getCmProgramForParallelProgramId(parallelProgId);
			if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format("+++ cmProg: ppID: %d: %s", parallelProgId, cmProg));

			StudentProgramModel spMdl = new StudentProgramModel();

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(String.format("+++ programType: %s, subjectId: %s",
		                     cmProg.getCmProgInfo().getProgramType().getType(), cmProg.getCmProgInfo().getSubjectId()));

			spMdl.setProgramType(cmProg.getCmProgInfo().getProgramType());
			spMdl.setSubjectId(cmProg.getCmProgInfo().getSubjectId());
			if (cmProg.getCustomProgId() > 0 || cmProg.getCustomQuizId() > 0) {
				CustomProgramComposite cpComp = spMdl.getCustom();
				cpComp.setCustomProgramId(cmProg.getCustomProgId());
				cpComp.setCustomQuizId(cmProg.getCustomQuizId());
			}
			String chapter = JsonUtil.getChapter(cmProg.getTestConfigJson());
			if (LOGGER.isDebugEnabled()) LOGGER.debug("+++ chapter: " + chapter);
		    stuDao.assignProgramToStudent(conn, userId, spMdl, chapter, String.valueOf(cmProg.getPassPercent()));

			CmProgramAssign cmProgAssign = new CmProgramAssign();
			cmProgAssign.setCmProgram(cmProg);
			cmProgAssign.setUserId(userId);
			cmProgAssign.setUserProgId(spMdl.getProgramId());
			ppDao.addProgramAssignment(cmProgAssign);
		}
		
		else {
			if (LOGGER.isDebugEnabled()) LOGGER.debug("+++ prevAssigned: " + prevAssigned);
			CmProgramAssign progAssign = ppDao.getProgramAssignForParallelProgIdAndUserId(parallelProgId, userId);
			if (LOGGER.isDebugEnabled()) LOGGER.debug("+++ progAssign: " + progAssign);
			
			// Update HA_USER
			StudentActiveInfo activeInfo = new StudentActiveInfo();
			activeInfo.setActiveRunId(progAssign.getRunId());
			activeInfo.setActiveRunSession(progAssign.getRunSession());
			activeInfo.setActiveSegment(progAssign.getProgSegment());
			activeInfo.setActiveSegmentSlot(progAssign.getSegmentSlot());
			activeInfo.setActiveTestId(progAssign.getTestId());
			
			stuDao.setActiveInfoAndUserProgId(userId, activeInfo, progAssign.getUserProgId());
			
		}
	}

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ParallelProgramLoginAction.class;
    }
    
}
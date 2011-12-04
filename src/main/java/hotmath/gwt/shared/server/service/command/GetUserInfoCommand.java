package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserInfo.AccountType;
import hotmath.gwt.cm_rpc.client.UserInfo.UserProgramCompletionAction;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentSettingsModel;
import hotmath.testset.ha.ChapterInfo;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaUserDao;
import hotmath.testset.ha.StudentUserProgramModel;

import java.sql.Connection;

import org.apache.log4j.Logger;

public class GetUserInfoCommand implements ActionHandler<GetUserInfoAction, UserLoginResponse> {

	private static final Logger logger = Logger.getLogger(GetUserInfoCommand.class);

    @Override
    public UserLoginResponse execute(final Connection conn, GetUserInfoAction action) throws Exception {
        try {
        	
            CmStudentDao sdao = CmStudentDao.getInstance();
            StudentModelI sm = sdao.getStudentModelBase(conn, action.getUserId(), true);
            StudentSettingsModel settings = sm.getSettings();
            
            
            CmProgramFlow cmProgram = new CmProgramFlow(conn, action.getUserId());
            StudentUserProgramModel userProgram = cmProgram.getUserProgram();
            StudentActiveInfo activeInfo = cmProgram.getActiveInfo();

            AccountType accountType = CmAdminDao.getInstance().getAccountType(conn, sm.getAdminUid());
            
            HaTestDefDao hdao = HaTestDefDao.getInstance();
            HaTestDef testDef = hdao.getTestDef(userProgram.getTestDefId());
            
            ChapterInfo chapterInfo = hdao.getChapterInfo(conn, userProgram);

            /** Create title sections.  The main title
             *  will contain the chapter # (if any).  The
             *  subtitle will contain the chapter title (if any)
             */
            String testTitle = testDef.getName();
            String subTitle=null;
            if(chapterInfo != null) {
            	/** do not add subtitle if chap program 
            	 * 
            	 */
                //testTitle += ", #" + chapterInfo.getChapterNumber();
                //subTitle = chapterInfo.getChapterTitle();
            }
            
            int programSegmentCount=testDef.getTotalSegmentCount();
            
            boolean isCustomProgram = testDef.getTestDefId() == CmProgram.CUSTOM_PROGRAM.getDefId();
            if(isCustomProgram) {
                CustomProgramInfo cpi = processCustomProgram(conn, action.getUserId(), activeInfo, userProgram);
                programSegmentCount = cpi.getProgramSegmentCount();
                testTitle = cpi.getTitle();
            }
            
            UserInfo userInfo = new UserInfo();
            userInfo.setUid(sm.getUid());
            userInfo.setTestId(activeInfo.getActiveTestId());
            userInfo.setRunId(activeInfo.getActiveRunId());
            userInfo.setProgramSegment(activeInfo.getActiveSegment());
            userInfo.setUserName(sm.getName());
            userInfo.setSessionNumber(activeInfo.getActiveRunSession());
            userInfo.setBackgroundStyle(sm.getBackgroundStyle());
            userInfo.setProgramName(testTitle);
            userInfo.setSubTitle(subTitle);
            userInfo.setShowWorkRequired(settings.getShowWorkRequired());
            userInfo.setTutoringAvail(settings.getTutoringAvailable());
            userInfo.setPassword(sm.getPasscode());
            userInfo.setLoginName(action.getLoginName());
            userInfo.setDemoUser(sm.getIsDemoUser());
            userInfo.setCustomProgram(isCustomProgram);
            
            int sessionCount=0;
            if(userInfo.getRunId() > 0) {
                if(isCustomProgram) {
                    sessionCount = CmCustomProgramDao.getInstance().getCustomProgramLessons(conn, userProgram.getCustomProgramId(), userInfo.getTestSegment()).size();
                }
                else {
                    sessionCount = HaTestRunDao.getInstance().getTestRunLessons(conn,userInfo.getRunId()).size();
                }
            }
            userInfo.setSessionCount(sessionCount);
                

            UserProgramCompletionAction onComplete = settings.getStopAtProgramEnd()?UserProgramCompletionAction.STOP:UserProgramCompletionAction.AUTO_ADVANCE;
            userInfo.setOnCompletion(onComplete);
            userInfo.setLimitGames(settings.getLimitGames());
            
            /**if is custom program or parallel program, then override the default EndOfProgram
             * action to force a stop.
             * 
             */
            boolean isParallelProgram = ParallelProgramDao.getInstance().isStudentInParallelProgram(action.getUserId());
            if(isCustomProgram || isParallelProgram) {
                /** this really depends on if CP ends on quiz or a test
                 * 
                 */
                userInfo.setOnCompletion(UserProgramCompletionAction.STOP);
            }

            
            /** set if either custom Quiz or custom program
             * 
             */
            userInfo.setCustomProgram(cmProgram.getUserProgram().isCustom());
            
            
            userInfo.setUserAccountType(accountType);
            userInfo.setPassPercentRequired(userProgram.getConfig().getPassPercent());
            userInfo.setProgramSegmentCount(programSegmentCount);
            userInfo.setViewCount(sdao.getTotalInmHViewCount(conn,action.getUserId()));
            UserLoginResponse userLoginResponse = new UserLoginResponse(userInfo, HaUserDao.getInstance().determineFirstDestination(conn, userInfo,cmProgram));
            return userLoginResponse;
            
        } catch (Exception e) {
        	logger.error(String.format("*** Error executing Action: %s", action.toString()), e);
            throw new CmRpcException(e);
        }
    }
    

    /** Generalize the initialization of the Custom Programs
     * 
     * @param conn
     * @param userId
     * @param activeInfo
     * @param userProgram
     * @return
     * @throws Exception
     */
    static public CustomProgramInfo processCustomProgram(final Connection conn, int userId, StudentActiveInfo activeInfo, StudentUserProgramModel userProgram) throws Exception {
        
        int programSegmentCount=0;
        if(activeInfo.getActiveRunId() == 0 && activeInfo.getActiveTestId() == 0) {
            
            /**
             *  One time per program use.
             * 
             *  This is done here because we do not want the user
             *  to be directed to a quiz, unless there is one in CP.
             *  
             *  
             *  Alternatively: ... I see no alternate because the flow is:
             *  
             *  1. The user logs in (this is called)
             *  2. This passes back the UserInfo object that defines
             *     the user's 'current active state'.
             *  3. The client will use this information and choose
             *     where to send the user.
             * 
             */

            /** Does this custom quiz start with a quiz?
             * 
             *  If not then create a dummy test as a place holder.
             */
            CmCustomProgramDao cpdao = CmCustomProgramDao.getInstance();
            if(!cpdao.doesProgramSegmentHaveQuiz(conn, userProgram.getCustomProgramId(), 1)) {
                activeInfo.setActiveSegment(1);
                activeInfo.setActiveTestId(0);
                activeInfo.setActiveRunSession(0);           

                HaTest custTest = HaTestDao.getInstance().createTest(userId,HaTestDefDao.getInstance().getTestDef(CmProgram.CUSTOM_PROGRAM.getDefId()), activeInfo.getActiveSegment());
                custTest.setProgramInfo(userProgram);
                HaTestRun testRun = HaTestDao.getInstance().createTestRun(conn,userId, custTest.getTestId(), 10,0,0);
                testRun.setHaTest(custTest);
                
                /** make sure the are lessons available for this test run.
                 * 
                 * If not then move to next segment, or return end of program?
                 */
                activeInfo.setActiveRunId(testRun.getRunId());
            }
            
            /** update the total number of program segments
             * 
             */
            programSegmentCount = CmCustomProgramDao.getInstance().getTotalSegmentCount(conn,userProgram.getCustomProgramId());
            
            /** save for next time */
            CmStudentDao.getInstance().setActiveInfo(conn, userId, activeInfo);
        }
        /** Get the Custom Program's test title assigned by user
         * 
         */
        CustomProgramModel customProgram = CmCustomProgramDao.getInstance().getCustomProgram(conn, userProgram.getCustomProgramId());
        programSegmentCount = CmCustomProgramDao.getInstance().getTotalSegmentCount(conn,userProgram.getCustomProgramId());
        return new CustomProgramInfo(programSegmentCount,customProgram.getProgramName());
    }
  
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserInfoAction.class;
    }
    
    
    public static class CustomProgramInfo {
        int programSegmentCount;
        String title;
        
        public CustomProgramInfo(int segCount, String title) {
            this.programSegmentCount = segCount;
            this.title = title;
        }

        public int getProgramSegmentCount() {
            return programSegmentCount;
        }

        public void setProgramSegmentCount(int programSegmentCount) {
            this.programSegmentCount = programSegmentCount;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }    
}



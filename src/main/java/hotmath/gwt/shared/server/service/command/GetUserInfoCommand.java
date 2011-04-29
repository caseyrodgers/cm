package hotmath.gwt.shared.server.service.command;

import hotmath.cm.program.CmProgramFlow;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserInfo.AccountType;
import hotmath.gwt.cm_rpc.client.UserInfo.UserProgramCompletionAction;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmDestination;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
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
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

public class GetUserInfoCommand implements ActionHandler<GetUserInfoAction, UserLoginResponse> {

	private static final Logger logger = Logger.getLogger(GetUserInfoCommand.class);

    @Override
    public UserLoginResponse execute(final Connection conn, GetUserInfoAction action) throws Exception {
        try {
            CmStudentDao sdao = new CmStudentDao();
            StudentModelI sm = sdao.getStudentModelBase(conn, action.getUserId(), true);
            StudentSettingsModel settings = sm.getSettings();
            
            
            CmProgramFlow cmProgram = new CmProgramFlow(conn, action.getUserId());
            StudentUserProgramModel userProgram = cmProgram.getUserProgram();
            StudentActiveInfo activeInfo = cmProgram.getActiveInfo();

            AccountType accountType = new CmAdminDao().getAccountType(conn, sm.getAdminUid());
            
            HaTestDefDao hdao = new HaTestDefDao();
            HaTestDef testDef = hdao.getTestDef(conn, userProgram.getTestDefId());
            
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
                    CmCustomProgramDao cpdao = new CmCustomProgramDao();
                    if(!cpdao.doesProgramSegmentHaveQuiz(conn, userProgram.getCustomProgramId(), 1)) {
                        activeInfo.setActiveSegment(1);
                        activeInfo.setActiveTestId(0);
                        activeInfo.setActiveRunSession(0);           

                        HaTest custTest = HaTestDao.createTest(conn, action.getUserId(),new HaTestDefDao().getTestDef(conn, CmProgram.CUSTOM_PROGRAM.getDefId()), activeInfo.getActiveSegment());
                        custTest.setProgramInfo(cmProgram.getUserProgram());
                        HaTestRun testRun = HaTestDao.createTestRun(conn, action.getUserId(), custTest.getTestId(), 10,0,0);
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
                    programSegmentCount = new CmCustomProgramDao().getTotalSegmentCount(conn,userProgram.getCustomProgramId());
                    
                    /** save for next time */
                    sdao.setActiveInfo(conn, action.getUserId(), activeInfo);
                }
                /** Get the Custom Program's test title assigned by user
                 * 
                 */
                CustomProgramModel customProgram = new CmCustomProgramDao().getCustomProgram(conn, userProgram.getCustomProgramId());
                programSegmentCount = new CmCustomProgramDao().getTotalSegmentCount(conn,userProgram.getCustomProgramId());
                testTitle = customProgram.getProgramName();
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
                    sessionCount = new CmCustomProgramDao().getCustomProgramLessons(conn, userProgram.getCustomProgramId(), userInfo.getTestSegment()).size();
                }
                else {
                    sessionCount = new HaTestRunDao().getTestRunLessons(conn,userInfo.getRunId()).size();
                }
            }
            userInfo.setSessionCount(sessionCount);
                

            UserProgramCompletionAction onComplete = settings.getStopAtProgramEnd()?UserProgramCompletionAction.STOP:UserProgramCompletionAction.AUTO_ADVANCE;
            userInfo.setOnCompletion(onComplete);
            userInfo.setLimitGames(settings.getLimitGames());
            
            /**if is custom program, then override the default EndOfProgram
             * action to force a stop.
             * 
             */
            if(isCustomProgram) {
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
            UserLoginResponse userLoginResponse = new UserLoginResponse(userInfo, determineFirstDestination(conn, userInfo,cmProgram));
            return userLoginResponse;
            
        } catch (Exception e) {
        	logger.error(String.format("*** Error executing Action: %s", action.toString()), e);
            throw new CmRpcException(e);
        }
    }
    
    /** what is the first thing the user is shown?
     *  
     *  This is based on the user's current active state.
     *  
     *  If user's program is complete:
     *     If stopAfterComplete, then 
     *        set firstAction to END_OF_PROGRAM
     *     else 
     *        // this should not happen ..?
     *        // unless error/corrupted ..?
     *        
     *  Else
     *      if runId > 0
     *          set firstAction to PRESCRIPTION
     *      else if testId > 0)
     *          set firstAction to QUIZ
     *                
     *      else
     *          set firstAction to WELCOME    
     *          
     *     
     */
    CmDestination firstDestination = null;
    private CmDestination determineFirstDestination(final Connection conn, UserInfo userInfo, CmProgramFlow programFlow) throws Exception {
        CmDestination destination = new CmDestination();

        if(userInfo.getRunId() > 0 && hasUserCompletedTestRun(conn, userInfo.getRunId())) {
            
            /** did the user pass this segment, if not
             *  then they must repeat it .. so program 
             *  is not complete.
             */
            HaTestRun testRun = new HaTestRunDao().lookupTestRun(conn, userInfo.getRunId());
            if(!testRun.isPassing()) {
                /** repeat the segment
                 * 
                 */
                programFlow.getActiveInfo().setActiveRunId(0);
                programFlow.getActiveInfo().setActiveTestId(0);
                destination.setPlace(CmPlace.QUIZ);
            }
            else {
                if(hasUserCompletedProgram(conn, userInfo, programFlow)) {
                    destination.setPlace(CmPlace.END_OF_PROGRAM);
                }
            }
        }
        else if(userInfo.getRunId() > 0) {
            destination.setPlace(CmPlace.PRESCRIPTION);
        }
        else if(userInfo.getTestId() > 0) {
            destination.setPlace(CmPlace.QUIZ);
        }
        else {
            destination.setPlace(CmPlace.WELCOME);
        }
        
        return destination;
    }
    
    
    /** Has this user completed the current active program ?
     * 
     * @param userInfo
     * @return
     * @throws Exception
     */
    private boolean hasUserCompletedProgram(final Connection conn, UserInfo userInfo, CmProgramFlow cmProgram) throws Exception {
        int totalSegments = userInfo.getProgramSegmentCount();
        int thisSegment = userInfo.getTestSegment();
        
        //assert(thisSegment > 0);
        
        /** program segments are 1 based
         * 
         */
        if(thisSegment <= totalSegments) {
            
            /** we know it is valid
             * 
             */
            if(thisSegment == totalSegments) {
                /** on last segment
                 * 
                 */
                
                /** has the user viewed each lesson in active prescription?
                 * 
                 */
                if(userInfo.getRunId() > 0) {
                    if(hasUserCompletedTestRun(conn, userInfo.getRunId())) {
                        return true;
                    }
                }
            }
        }
        else {
            return true;  // over the end, bug?
        }

        /** default is not completed 
         * 
         */
        return false;
    }

    /** This test run is fully completed
    */

    private boolean hasUserCompletedTestRun(final Connection conn, int runId) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_COUNT_UNCOMPLETED_TEST_RUN_LESSONS_PROGRAM");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, runId);
            ResultSet rs = ps.executeQuery();
            rs.first();
            int cntToView = rs.getInt(1);
            
            return (cntToView == 0);
        }
        finally {
            SqlUtilities.releaseResources(null,ps, null);
        }
    }

    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserInfoAction.class;
    }
}

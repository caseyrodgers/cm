package hotmath.gwt.shared.server.service.command;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetUserInfoAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.UserInfo;
import hotmath.gwt.shared.client.util.UserInfo.AccountType;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.ChapterInfo;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.StudentUserProgramModel;

import java.sql.Connection;

public class GetUserInfoCommand implements ActionHandler<GetUserInfoAction, UserInfo> {

    @Override
    public UserInfo execute(final Connection conn, GetUserInfoAction action) throws Exception {
        try {
            CmStudentDao dao = new CmStudentDao();
            StudentModelI sm = dao.getStudentModelBasic(conn, action.getUserId());
            
            CmUserProgramDao upDao = new CmUserProgramDao();
            StudentUserProgramModel userProgram = upDao.loadProgramInfoCurrent(conn, action.getUserId());
            StudentActiveInfo activeInfo = dao.loadActiveInfo(conn, action.getUserId());
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
                testTitle += ", #" + chapterInfo.getChapterNumber();
                subTitle = chapterInfo.getChapterTitle();
            }
            
            
            
            /** if Custom Program, the force user to prescription .. there are no
             *  quizzes.
             */
            boolean isCustomProgram = testDef.getTestDefId() == CmProgram.CUSTOM_PROGRAM.getDefId();
            if(isCustomProgram) {
                if(activeInfo.getActiveRunId() == 0) {
                    /** create prescription 
                     * 
                     *  This is done here because we do not want the user
                     *  to be directed to a quiz because no active information
                     *  would have been set.
                     *  
                     *  Alternatively: ... I see no alternate because the flow is:
                     *  
                     *  1. The user logs in (this is called)
                     *  2. This passed back the UserInfo object that defines
                     *     the user's current active state.
                     *  3. The client will use this information and choose
                     *     where to send the user.
                     *     
                     *  (question: does the client know it is a custom program?)
                     *  @TODO: send the Program Name as the custom name
                     * 
                     */
                    HaTest custTest = HaTestDao.createTest(conn, action.getUserId(),new HaTestDefDao().getTestDef(conn, CmProgram.CUSTOM_PROGRAM.getDefId()), -1);
                    custTest.setProgramInfo(userProgram);
                    HaTestRun testRun = HaTestDao.createTestRun(conn, action.getUserId(), custTest.getTestId(), 0,0,0);
                    testRun.setHaTest(custTest);
                    
                    activeInfo.setActiveTestId(0);
                    activeInfo.setActiveRunId(testRun.getRunId());
                    
                    /** save for next time */
                    dao.setActiveInfo(conn, action.getUserId(), activeInfo);
                }
                /** Get the Custom Program's test title assigned by user
                 * 
                 */
                CustomProgramModel customProgram = new CmCustomProgramDao().getCustomProgram(conn, userProgram.getCustomProgramId());
                testTitle = customProgram.getProgramName();
            }
            
            UserInfo userInfo = new UserInfo();
            userInfo.setUid(sm.getUid());
            userInfo.setTestId(activeInfo.getActiveTestId());
            userInfo.setRunId(activeInfo.getActiveRunId());
            userInfo.setTestSegment(activeInfo.getActiveSegment());
            userInfo.setUserName(sm.getName());
            userInfo.setSessionNumber(activeInfo.getActiveRunSession());
            userInfo.setBackgroundStyle(sm.getBackgroundStyle());
            userInfo.setTestName(testTitle);
            userInfo.setSubTitle(subTitle);
            userInfo.setShowWorkRequired(sm.getShowWorkRequired());
            userInfo.setTutoringAvail(sm.getTutoringAvail());
            userInfo.setPassword(sm.getPasscode());
            userInfo.setLoginName(null);
            userInfo.setDemoUser(sm.getIsDemoUser());
            userInfo.setCustomProgram(isCustomProgram);

            /** Set number of sessions in current prescription */
            if(userInfo.getRunId() > 0)
                userInfo.setSessionCount(new HaTestRunDao().getTestRunLessons(conn,userInfo.getRunId()).size());
            
            userInfo.setUserAccountType(accountType);
            
            userInfo.setPassPercentRequired(userProgram.getConfig().getPassPercent());
            userInfo.setTestSegmentCount(testDef.getTotalSegmentCount());
            userInfo.setViewCount(dao.getTotalInmHViewCount(conn,action.getUserId()));
            
            return userInfo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException(e);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserInfoAction.class;
    }
}

package hotmath.gwt.shared.server.service;

import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.CreateTestRunAction;
import hotmath.testset.ha.CmProgram;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

public class LoadTester {

    static public String loadTestCreatePrescription() throws Exception {

        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();

            int _userId = CmTestUtils.setupDemoAccount(CmProgram.PREALG_PROF);

            CmStudentDao _dao = new CmStudentDao();
            StudentModelI _sm = _dao.getStudentModelBase(conn, _userId);

            long timeStart = System.currentTimeMillis();

            String guids[] = { "cahseehm_1_1_PracticeTest_1_1",
                    "cahseehm_1_1_PracticeTest_10_1",
                    "cahseehm_1_1_PracticeTest_2_1",
                    "cahseehm_1_1_PracticeTest_3_1",
                    "cahseehm_1_1_PracticeTest_4_1",
                    "cahseehm_1_1_PracticeTest_5_1",
                    "cahseehm_1_1_PracticeTest_6_1",
                    "cahseehm_1_1_PracticeTest_7_1",
                    "cahseehm_1_1_PracticeTest_8_1",
                    "cahseehm_1_1_PracticeTest_9_1" };

            ActionDispatcher dispatcher = ActionDispatcher.getInstance();

            UserLoginResponse loginResponse = dispatcher.execute(new GetUserInfoAction(_userId,"Testing"));
            UserInfo user = loginResponse.getUserInfo();
            _dao.assignProgramToStudent(conn, user.getUid(), CmProgram.CAHSEEHM, null);
            

            QuizHtmlResult result = dispatcher.execute(new GetQuizHtmlAction(new CmProgramFlow(conn,_userId).getActiveInfo().getActiveTestId()));
            int testId = result.getTestId();

            CreateTestRunResponse userInfo = dispatcher.execute(new CreateTestRunAction(testId, user.getUid()));

            int runId = userInfo.getRunId();

            PrescriptionSessionResponse response = dispatcher.execute(new GetPrescriptionAction(runId, 1, true));

            long timeComplete = (System.currentTimeMillis() - timeStart) / 1000;

            _dao.removeUser(conn, _sm);

            return "Run time: " + timeComplete;
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }
}

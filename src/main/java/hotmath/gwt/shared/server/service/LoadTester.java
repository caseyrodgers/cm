package hotmath.gwt.shared.server.service;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.CreateTestRunAction;
import hotmath.gwt.shared.client.rpc.action.GetPrescriptionAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlAction;
import hotmath.gwt.shared.client.rpc.action.GetUserInfoAction;
import hotmath.gwt.shared.client.rpc.result.CreateTestRunResponse;
import hotmath.gwt.shared.client.rpc.result.QuizHtmlResult;
import hotmath.gwt.shared.client.util.UserInfo;
import hotmath.testset.ha.CmProgram;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

public class LoadTester {

    static public String loadTestCreatePrescription() throws Exception {

        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();

            int _userId = CmTestUtils.setupDemoAccount();

            CmStudentDao _dao = new CmStudentDao();
            StudentModelI _sm = _dao.getStudentModel(_userId);

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

            UserInfo user = dispatcher.execute(new GetUserInfoAction(_userId));

            _dao.assignProgramToStudent(conn, user.getUid(), CmProgram.CAHSEEHM, null);
            

            QuizHtmlResult result = dispatcher.execute(new GetQuizHtmlAction(user.getUid(),0, 1));
            int testId = result.getTestId();

            CreateTestRunResponse userInfo = dispatcher.execute(new CreateTestRunAction(testId));

            int runId = userInfo.getRunId();

            RpcData rdata = dispatcher.execute(new GetPrescriptionAction(runId, 1, true));

            long timeComplete = (System.currentTimeMillis() - timeStart) / 1000;

            _dao.removeUser(conn, _sm);

            return "Run time: " + timeComplete;
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }

}

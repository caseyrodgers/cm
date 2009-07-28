package hotmath.gwt.shared.server.service;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.CreateTestRunAction;
import hotmath.gwt.shared.client.rpc.action.GetPrescriptionAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlAction;
import hotmath.gwt.shared.client.rpc.action.GetUserInfoAction;
import hotmath.gwt.shared.client.util.RpcData;
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

            RpcData quizRpcData = dispatcher.execute(new GetQuizHtmlAction(user.getUid(), 1));
            int testId = quizRpcData.getDataAsInt("test_id");

            RpcData rpcData = dispatcher.execute(new CreateTestRunAction(testId));

            int runId = rpcData.getDataAsInt("run_id");

            RpcData rdata = dispatcher.execute(new GetPrescriptionAction(runId, 1, true));

            long timeComplete = (System.currentTimeMillis() - timeStart) / 1000;

            _dao.removeUser(_sm);

            return "Run time: " + timeComplete;
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }

}

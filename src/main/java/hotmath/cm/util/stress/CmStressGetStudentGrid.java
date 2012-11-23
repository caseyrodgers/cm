package hotmath.cm.util.stress;

import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;

public class CmStressGetStudentGrid implements StressTest {
    public CmStressGetStudentGrid() {

    }

    @Override
    public void runTest(int aid, int uid, String uName, String uPass) throws Exception {
        GetStudentGridPageAction pageAction = new GetStudentGridPageAction(aid, null);
        ActionDispatcher.getInstance().execute(pageAction);
    }

}

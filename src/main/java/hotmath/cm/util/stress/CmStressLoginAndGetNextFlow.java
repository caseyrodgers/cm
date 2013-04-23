package hotmath.cm.util.stress;

import hotmath.gwt.cm_rpc.client.rpc.AutoAdvanceUserAction;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;

/**
 * 
 * Test Login and GET the CURRENT flow action in user's current program.
 * 
 * 
 */
public class CmStressLoginAndGetNextFlow implements StressTest {
    public CmStressLoginAndGetNextFlow() {

    }

    @Override
    public void runTest(int aid, int uid, String uName, String uPass) throws Exception {

        GetCmProgramFlowAction flowAction = new GetCmProgramFlowAction(uid, FlowType.NEXT);
        CmProgramFlowAction flowActive = ActionDispatcher.getInstance().execute(flowAction);

        switch (flowActive.getPlace()) {
        case AUTO_ADVANCED_PROGRAM:
            doAutoAdvance(uid);
            break;

        case AUTO_PLACEMENT:
            break;

        case END_OF_PROGRAM:
            break;

        case PRESCRIPTION:
            assert (flowActive.getPrescriptionResponse() != null);
            break;

        case QUIZ:
            assert (flowActive.getQuizResult() != null);
            break;
        }
    }

    
    private void doAutoAdvance(int uid) throws Exception {
        AutoAdvanceUserAction action = new AutoAdvanceUserAction(uid);
        ActionDispatcher.getInstance().execute(action);
    }
}

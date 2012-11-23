package hotmath.cm.util.stress;

import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.data.HaUserLoginInfo;
import hotmath.gwt.shared.client.rpc.action.LoginAction;

import org.apache.log4j.Logger;

/**
 * 
 * Test Login and GET the CURRENT flow action in user's current program.
 * 
 * 
 */
public class CmStressImplDefault implements StressTest {

    final static Logger __logger = Logger.getLogger(CmStressImplDefault.class);

    public CmStressImplDefault() {

    }

    @Override
    public void runTest(int uid, String uName, String uPass) throws Exception {

        LoginAction login = new LoginAction(uName, uPass);
        HaUserLoginInfo loginInfo = ActionDispatcher.getInstance().execute(login);

        int userId = loginInfo.getHaLoginInfo().getUserId();

        GetCmProgramFlowAction flowAction = new GetCmProgramFlowAction(userId, FlowType.ACTIVE);
        CmProgramFlowAction flowActive = ActionDispatcher.getInstance().execute(flowAction);

        switch (flowActive.getPlace()) {
        case AUTO_ADVANCED_PROGRAM:
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

}

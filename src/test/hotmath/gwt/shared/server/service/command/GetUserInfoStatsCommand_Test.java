package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoStatsAction;
import hotmath.gwt.cm_rpc.client.rpc.UserInfoStats;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import junit.framework.TestCase;

public class GetUserInfoStatsCommand_Test extends TestCase {
    
    public GetUserInfoStatsCommand_Test(String name) {
        super(name);
    }
    
    public void testIt() throws Exception {
        GetUserInfoStatsAction action = new GetUserInfoStatsAction(1008728);
        UserInfoStats res = ActionDispatcher.getInstance().execute(action);
        assertTrue(res.getTutorStats().getCorrectPercent() > 0);
    }

}

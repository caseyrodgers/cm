package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.rpc.action.GetQuizCurrentResultsAction;

public class GetQuizCurrentResultsCommand_Test extends CmDbTestCase {
    
    public GetQuizCurrentResultsCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_testRun == null)
            setupDemoAccountTestRun();
    }
    public void testCreate() throws Exception {
        GetQuizCurrentResultsAction action = new GetQuizCurrentResultsAction(_test.getTestId());
        CmList<RpcData> rdata = new GetQuizCurrentResultsCommand().execute(conn, action);
        assertNotNull(rdata);
    }
    
    public void testDispatch() throws Exception {
        GetQuizCurrentResultsAction action = new GetQuizCurrentResultsAction(_test.getTestId());
        CmList<RpcData> rdata = ActionDispatcher.getInstance().execute(action);
        assertNotNull(rdata);
    }
}

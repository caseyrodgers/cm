package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetQuizCurrentResultsAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionDispatcher;

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
        GetQuizCurrentResultsAction action = new GetQuizCurrentResultsAction(_user.getUid());
        CmList<RpcData> rdata = new GetQuizCurrentResultsCommand().execute(conn, action);
        assertNotNull(rdata);
    }
    
    public void testDispatch() throws Exception {
        GetQuizCurrentResultsAction action = new GetQuizCurrentResultsAction(_user.getUid());
        CmList<RpcData> rdata = ActionDispatcher.getInstance().execute(action);
        assertNotNull(rdata);
    }
}

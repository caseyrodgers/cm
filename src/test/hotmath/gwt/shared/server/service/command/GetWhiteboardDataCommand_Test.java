package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;

public class GetWhiteboardDataCommand_Test extends CmDbTestCase {
    
    public GetWhiteboardDataCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_testRun == null)
            setupDemoAccountTestRun();
    }
    
    public void testGetData() throws Exception {
        String testPid = _testRun.getTestRunResults().get(0).getPid();
        GetWhiteboardDataAction action = new GetWhiteboardDataAction(_user.getUid(),testPid, _testRun.getRunId());
        CmList<WhiteboardCommand> whiteboard = new GetWhiteboardDataCommand().execute(conn, action);
        assertNotNull(whiteboard);
        
        assertTrue(whiteboard.size() > 0);
    }

    public void testGetDataThroughActionDispatcher() throws Exception {
        String testPid = _testRun.getTestRunResults().get(0).getPid();
        GetWhiteboardDataAction action = new GetWhiteboardDataAction(_user.getUid(),testPid, _testRun.getRunId());
        CmList<WhiteboardCommand> whiteboard =  ActionDispatcher.getInstance().execute(action);
        assertNotNull(whiteboard);
        
        assertTrue(whiteboard.size() > 0);
    }
}

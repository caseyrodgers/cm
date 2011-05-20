package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.ResetStudentActivityAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

public class ResetStudentActivityCommand_Test extends CmDbTestCase {
    
    public ResetStudentActivityCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_test == null)
            setupDemoAccountTest();
    }
    
    
    public void testReset() throws Exception {
        ResetStudentActivityAction action = new ResetStudentActivityAction(_user.getUid(), _test.getTestId(), 0);
        RpcData data = new ResetStudentActivityCommand().execute(conn,action);
        assertTrue(data.getDataAsString("status").equals("OK"));
    }

}

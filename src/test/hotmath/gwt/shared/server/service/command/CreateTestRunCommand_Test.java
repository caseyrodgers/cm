package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;

public class CreateTestRunCommand_Test extends CmDbTestCase {
    
    public CreateTestRunCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_test == null) {
            setupDemoAccountTest();
        }
    }
    
    public void testCreateTestRun() throws Exception {
        CreateTestRunAction action = new CreateTestRunAction(_test.getTestId(),_user.getUid());
        CreateTestRunResponse response = new CreateTestRunCommand().execute(conn, action);
        assertTrue(response.getNextAction().getPlace() == CmPlace.PRESCRIPTION);
    }

}

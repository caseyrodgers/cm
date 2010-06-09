package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.rpc.action.SimulateCommunicationsExceptionTestAction;

import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;

public class SimulateCommunicationsExceptionCommand_Test extends CmDbTestCase {
	
	public SimulateCommunicationsExceptionCommand_Test(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testActionDispatcherRetry() throws Exception {
        SimulateCommunicationsExceptionTestAction action = new SimulateCommunicationsExceptionTestAction();
        boolean result = false;
        try {
            ActionDispatcher.getInstance().execute(action);
        }
        catch (CmRpcException cre) {
        	result = (cre.getMessage().indexOf("Simulating a CommunicationsException") > -1);
        }
        
        assertTrue(result);
	}
}

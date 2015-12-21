package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.NewMobileUserAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import junit.framework.TestCase;

public class NewMobileUserCommand_Test extends TestCase {
	
	public NewMobileUserCommand_Test(String name) {
		super(name);
	}
	
	public void testOn() throws Exception {
		RpcData newMobileUser = ActionDispatcher.getInstance().execute(new NewMobileUserAction());
		assertTrue(newMobileUser.getDataAsInt("uid") > 0);
	}
}

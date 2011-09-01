package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.AutoAdvanceUserAction;
import hotmath.gwt.cm_rpc.client.rpc.AutoUserAdvanced;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;

public class AutoAdvanceUserCommand_Test extends CmDbTestCase {
	
	public AutoAdvanceUserCommand_Test(String name) {
		super(name);
	}
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		if(_user == null)
			setupDemoAccount();
	}
		
	
	public void testAutoAdvance() throws Exception {
        AutoAdvanceUserAction action = new AutoAdvanceUserAction(_user.getUid());
        AutoUserAdvanced advanced = ActionDispatcher.getInstance().execute(action);
        assertNotNull(advanced);
	}
}

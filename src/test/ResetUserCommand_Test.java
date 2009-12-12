import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionDispatcher;
import hotmath.gwt.shared.server.service.command.ResetUserCommand;


public class ResetUserCommand_Test extends CmDbTestCase {
	
	public ResetUserCommand_Test(String name){
		super(name);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		if(_user == null)
			setupDemoAccount();
	}
	
	
	public void testCreate() throws Exception {
		ResetUserAction a = new ResetUserAction(_user.getUid());
		RpcData r = new ResetUserCommand().execute(conn,a);
		assertTrue(r.getDataAsString("status").equals("OK"));
	}
	
	public void testDispatch() throws Exception {
		ResetUserAction a = new ResetUserAction(_user.getUid());
		RpcData r = ActionDispatcher.getInstance().execute(a);
		assertTrue(r.getDataAsString("status").equals("OK"));
	}


}

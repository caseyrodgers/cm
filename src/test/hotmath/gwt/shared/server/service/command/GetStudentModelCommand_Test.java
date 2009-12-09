package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;
import hotmath.gwt.shared.server.service.ActionDispatcher;

public class GetStudentModelCommand_Test extends CmDbTestCase {
    
    public GetStudentModelCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_user == null)
            setupDemoAccount();
    }
    public void testCreate() throws Exception {
        GetStudentModelAction action = new GetStudentModelAction(_user.getUid());
        StudentModelI sm = new GetStudentModelCommand().execute(conn, action);
        assertTrue(sm.getUid().equals(_user.getUid()));
    }
    
    public void testDispatch() throws Exception {
        GetStudentModelAction action = new GetStudentModelAction(_user.getUid());
        StudentModelI sm = ActionDispatcher.getInstance().execute(action);
        assertTrue(sm.getUid().equals(_user.getUid()));
    }
}

package hotmath.gwt.shared.server.service.command;

import java.util.List;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWorkForUserAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;

public class GetAssignmentWorkForUserCommand_Test extends CmDbTestCase {

    public GetAssignmentWorkForUserCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_test == null)
            setupDemoAccountTest();
    }
    
    public void testGetAssignnentWork() throws Exception {
    	GetAssignmentWorkForUserAction action = new GetAssignmentWorkForUserAction();
    	action.setUid(_test.getUser().getUid());
    	
    	GetAssignmentWorkForUserCommand cmd = new GetAssignmentWorkForUserCommand();
    	List<StudentAssignment> list = cmd.execute(conn, action);
    	
    	assertNotNull(list);
    	//assertTrue(list.size()>0);
    }
    
}

package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.Assignment;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsCreatedAction;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;

public class GetAssignmentsCreatedCommand_Test extends TestCase {
    
    public GetAssignmentsCreatedCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        GetAssignmentsCreatedAction action = new GetAssignmentsCreatedAction(2);
        CmList<Assignment> assignments = new GetAssignmentsCreatedCommand().execute(HMConnectionPool.getConnection(), action);
        
        assertTrue(assignments.size() > 0);
    }

}

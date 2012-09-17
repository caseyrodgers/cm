package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.UnassignStudentsFromAssignmentAction;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;

public class UnassignStudentsFromAssignmentCommand_Test extends TestCase {
    
    public UnassignStudentsFromAssignmentCommand_Test(String name) {
        super(name);
    }
    
    public void testId() throws Exception {
        UnassignStudentsFromAssignmentAction action = new UnassignStudentsFromAssignmentAction(0);
        action.getStudents().add(new StudentAssignment());
        RpcData data = new UnassignStudentsFromAssignmentCommand().execute(HMConnectionPool.getConnection(), action);
        assertTrue(data.getDataAsString("status").equals("OK"));
    }
}

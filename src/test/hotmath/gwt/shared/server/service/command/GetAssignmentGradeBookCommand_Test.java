package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;

public class GetAssignmentGradeBookCommand_Test extends TestCase {
    
    public GetAssignmentGradeBookCommand_Test(String name) {
        super(name);
    }
    
    public void testIt() throws Exception {
        GetAssignmentGradeBookAction action = new GetAssignmentGradeBookAction(7);
        CmList<StudentAssignment> students = new GetAssignmentGradeBookCommand().execute(HMConnectionPool.getConnection(), action);
        assertNotNull(students);
    }

}

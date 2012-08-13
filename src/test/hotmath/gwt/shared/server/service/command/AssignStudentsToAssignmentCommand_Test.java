package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc.client.rpc.AssignStudentsToAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;

public class AssignStudentsToAssignmentCommand_Test extends TestCase {
    
    public AssignStudentsToAssignmentCommand_Test(String name) {
        super(name);
    }
    
    public void testIt() throws Exception {
        
        int assignKey = 2;
        CmList<StudentDto> students = new CmArrayList<StudentDto>();
        students.add(new StudentDto(1,"Test"));
        AssignStudentsToAssignmentAction action = new AssignStudentsToAssignmentAction(assignKey,students);
        new AssignStudentsToAssignmentCommand().execute(HMConnectionPool.getConnection(), action);
    }

}

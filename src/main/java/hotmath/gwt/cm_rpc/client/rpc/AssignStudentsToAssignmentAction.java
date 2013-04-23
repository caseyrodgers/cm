package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class AssignStudentsToAssignmentAction implements Action<AssignmentInfo>{
    
    private int assignKey;
    private CmList<StudentDto> students;

    public AssignStudentsToAssignmentAction(){}
    
    public AssignStudentsToAssignmentAction(int assignKey, CmList<StudentDto> students) {
        this.assignKey = assignKey;
        this.students = students;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public CmList<StudentDto> getStudents() {
        return students;
    }

    public void setStudents(CmList<StudentDto> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "AssignStudentsToAssignmentAction [assignKey=" + assignKey + ", students=" + students + "]";
    }
}

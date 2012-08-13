package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentDto;

public class UnassignStudentsFromAssignmentAction implements Action<RpcData> {
    
    private int assKey;
    private CmList<StudentDto> students = new CmArrayList<StudentDto>();

    public UnassignStudentsFromAssignmentAction(){}
    
    public UnassignStudentsFromAssignmentAction(int assKey) {
        this.assKey = assKey;
    }

    public int getAssKey() {
        return assKey;
    }

    public void setAssKey(int assKey) {
        this.assKey = assKey;
    }

    public CmList<StudentDto> getStudents() {
        return students;
    }

    public void setStudents(CmList<StudentDto> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "UnassignStudentsFromAssignmentAction [assKey=" + assKey + ", students=" + students + "]";
    }

}

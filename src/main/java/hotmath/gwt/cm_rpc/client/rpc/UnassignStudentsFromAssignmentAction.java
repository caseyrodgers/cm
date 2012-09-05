package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;

public class UnassignStudentsFromAssignmentAction implements Action<RpcData> {
    
    private int assKey;
    private CmList<StudentAssignment> students = new CmArrayList<StudentAssignment>();

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

    public CmList<StudentAssignment> getStudents() {
        return students;
    }

    public void setStudents(CmList<StudentAssignment> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "UnassignStudentsFromAssignmentAction [assKey=" + assKey + ", students=" + students + "]";
    }

}

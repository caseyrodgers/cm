package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

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

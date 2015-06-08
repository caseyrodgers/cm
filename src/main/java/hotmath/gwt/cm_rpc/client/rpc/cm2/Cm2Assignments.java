package hotmath.gwt.cm_rpc.client.rpc.cm2;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;

import java.util.List;

public class Cm2Assignments {
    
    private List<StudentAssignmentInfo> assignments;

    public Cm2Assignments() {}
    
    public Cm2Assignments(List<StudentAssignmentInfo> asses) {
        this.assignments = asses;
    }

    public List<StudentAssignmentInfo> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<StudentAssignmentInfo> assignments) {
        this.assignments = assignments;
    }

}

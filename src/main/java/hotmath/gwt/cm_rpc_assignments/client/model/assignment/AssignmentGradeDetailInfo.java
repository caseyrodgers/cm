package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class AssignmentGradeDetailInfo implements Response {
    
    private int numStudentsGraded;
    private int numStudentsInAssignment;

    public AssignmentGradeDetailInfo(){}

    public AssignmentGradeDetailInfo(int numStudentsGraded, int numStudentsInAssignment) {
        this.numStudentsGraded = numStudentsGraded;
        this.numStudentsInAssignment = numStudentsInAssignment;
    }

    public int getNumStudentsGraded() {
        return numStudentsGraded;
    }

    public void setNumStudentsGraded(int numStudentsGraded) {
        this.numStudentsGraded = numStudentsGraded;
    }

    public int getNumStudentsInAssignment() {
        return numStudentsInAssignment;
    }

    public void setNumStudentsInAssignment(int numStudentsInAssignment) {
        this.numStudentsInAssignment = numStudentsInAssignment;
    }
    
    public String getGradedStatus() {
        return numStudentsGraded + " of " + numStudentsInAssignment;
    }
}

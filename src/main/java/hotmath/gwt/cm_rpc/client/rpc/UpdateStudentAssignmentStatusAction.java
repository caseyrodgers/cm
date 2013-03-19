package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;

public class UpdateStudentAssignmentStatusAction implements Action<RpcData> {

    private StudentAssignment stuAssignment;
    private boolean releaseGrades;

    public UpdateStudentAssignmentStatusAction(){}
    
    public UpdateStudentAssignmentStatusAction(StudentAssignment stuAssign, boolean releaseGrades) {
        this.stuAssignment = stuAssign;
        this.releaseGrades = releaseGrades;
    }

    public StudentAssignment getStudentAssignment() {
        return stuAssignment;
    }

    public void setStudentAssignment(StudentAssignment stuAssign) {
        this.stuAssignment = stuAssign;
    }

    public boolean isReleaseGrades() {
        return releaseGrades;
    }

    public void setReleaseGrades(boolean releaseGrades) {
        this.releaseGrades = releaseGrades;
    }

    @Override
    public String toString() {
    	int assignKey = (stuAssignment != null) ? stuAssignment.getAssignment().getAssignKey() : 0;
    	int uid = (stuAssignment != null) ? stuAssignment.getUid() : 0;
        return "UpdateStudentAssignmentStatusAction [assignKey=" + assignKey + ", uid=" + uid + "]";
    }

}

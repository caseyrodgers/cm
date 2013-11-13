package hotmath.gwt.cm_rpc_assignments.client.model;

import hotmath.gwt.cm_core.client.model.StudentAssignmentProblemStat;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.List;

public class AssignmentRealTimeStatsUsers implements Response {
    
    private List<StudentAssignmentProblemStat> students;

    public AssignmentRealTimeStatsUsers() {}
    
    public AssignmentRealTimeStatsUsers(List<StudentAssignmentProblemStat> studentStatus ) {
        this.students = studentStatus;
    }

    public List<StudentAssignmentProblemStat> getStudents() {
        return students;
    }

    public void setStudents(List<StudentAssignmentProblemStat> students) {
        this.students = students;
    }


}

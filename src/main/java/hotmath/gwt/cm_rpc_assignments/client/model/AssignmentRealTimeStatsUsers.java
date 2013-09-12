package hotmath.gwt.cm_rpc_assignments.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.StudentModel;

import java.util.List;

public class AssignmentRealTimeStatsUsers implements Response {
    
    private List<StudentModel> students;

    public AssignmentRealTimeStatsUsers() {}
    
    public AssignmentRealTimeStatsUsers(List<StudentModel> students ) {
        this.students = students;
    }

    public List<StudentModel> getStudents() {
        return students;
    }

    public void setStudents(List<StudentModel> students) {
        this.students = students;
    }


}

package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.StudentModelI;

import java.util.List;

public class GradeBookModel implements Response {
    
    private int assignKey;
    private List<StudentModelI> students;

    public GradeBookModel(){}
    
    public GradeBookModel(int assignKey, List<StudentModelI> students){
        this.assignKey = assignKey;
        this.students = students;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public List<StudentModelI> getStudents() {
        return students;
    }

    public void setStudents(List<StudentModelI> students) {
        this.students = students;
    }

}

package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

public class StudentDto implements Response {
    
    private int uid;
    private String name;
    List<StudentProblemDto> problems = new ArrayList<StudentProblemDto>();

    public StudentDto(){}
    
    public StudentDto(int uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public List<StudentProblemDto> getProblems() {
        return problems;
    }

    public void setProblems(List<StudentProblemDto> problems) {
        this.problems = problems;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StudentDto [uid=" + uid + ", name=" + name + "]";
    }

}

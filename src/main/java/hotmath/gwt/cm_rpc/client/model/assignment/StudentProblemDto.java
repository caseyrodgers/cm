package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class StudentProblemDto implements Response {

    private int uid;
    private ProblemDto problem;
    private String status;

    
    public StudentProblemDto() {
    }

    public StudentProblemDto(int uid, ProblemDto problem, String status) {
        this.uid = uid;
        this.problem = problem;
        this.status = status;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public ProblemDto getProblem() {
        return problem;
    }

    public void setProblem(ProblemDto problem) {
        this.problem = problem;
    }


    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPidLabel() {
        return getProblem().getLabel();
    }
    
    public String getPid() {
        return problem.getPid();
    }

    @Override
    public String toString() {
        return "StudentProblemDto [uid=" + uid + ", problem=" + problem + ", status=" + status + "]";
    }
}

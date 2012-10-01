package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;


/** Composite of a solutionInfo and the problem type
 * 
 * @author casey
 *
 */
public class AssignmentProblem implements Response{
    
    private SolutionInfo info;
    private ProblemType problemType;
    private int userId;
    private int assignKey;

    public AssignmentProblem(){}
    
    public AssignmentProblem(int userId, int assignKey, SolutionInfo info, ProblemType problemType) {
        this.userId = userId;
        this.assignKey = assignKey;
        this.info = info;
        this.problemType = problemType;
    }

    public SolutionInfo getInfo() {
        return info;
    }

    public void setInfo(SolutionInfo info) {
        this.info = info;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    @Override
    public String toString() {
        return "AssignmentProblem [info=" + info + ", problemType=" + problemType + ", userId=" + userId
                + ", assignKey=" + assignKey + "]";
    }
}

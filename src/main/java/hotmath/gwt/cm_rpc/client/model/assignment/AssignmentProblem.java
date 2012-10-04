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
    private String lastUserWidgetValue;

    public AssignmentProblem(){}
    
    public AssignmentProblem(int userId, int assignKey, SolutionInfo info, ProblemType problemType, String lastUserWidgetValue) {
        this.userId = userId;
        this.assignKey = assignKey;
        this.info = info;
        this.problemType = problemType;
        this.lastUserWidgetValue = lastUserWidgetValue;
    }

    public SolutionInfo getInfo() {
        return info;
    }

    public String getLastUserWidgetValue() {
        return lastUserWidgetValue;
    }

    public void setLastUserWidgetValue(String lastUserWidgetValue) {
        this.lastUserWidgetValue = lastUserWidgetValue;
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
                + ", assignKey=" + assignKey + ", lastUserWidgetValue=" + lastUserWidgetValue + "]";
    }

}

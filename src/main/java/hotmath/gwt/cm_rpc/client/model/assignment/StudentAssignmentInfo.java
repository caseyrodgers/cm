package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.Date;


/** User's state information about a single Assignment
 * 
 * @author casey
 *
 */
public class StudentAssignmentInfo implements Response {
    
    private int uid;
    private int assignKey;
    private Date dueDate;
    private boolean isGraded;
    private String comments;
    private String status;
    private Date turnInDate;
    private int cntProblems;
    private int cntSubmitted;

    public StudentAssignmentInfo() {}
    
    public StudentAssignmentInfo(int assignKey, int uid, boolean graded, Date turnInDate, String status, Date dueDate, String comments, int cntProblems, int cntSubmitted) {
        this.assignKey = assignKey;
        this.uid = uid;
        this.isGraded = graded;
        this.turnInDate = turnInDate;
        this.status = status;
        this.dueDate = dueDate;
        this.comments = comments;
        this.cntProblems = cntProblems;
        this.cntSubmitted = cntSubmitted;
    }

    
    public boolean isComplete() {
        return cntProblems == cntSubmitted;
    }

    public int getCntProblems() {
        return cntProblems;
    }

    public void setCntProblems(int cntProblems) {
        this.cntProblems = cntProblems;
    }

    public int getCntSubmitted() {
        return cntSubmitted;
    }

    public void setCntSubmitted(int cntSubmitted) {
        this.cntSubmitted = cntSubmitted;
    }


    public Date getTurnInDate() {
        return turnInDate;
    }

    public void setTurnInDate(Date turnInDate) {
        this.turnInDate = turnInDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public boolean isGraded() {
        return isGraded;
    }

    public void setGraded(boolean isGraded) {
        this.isGraded = isGraded;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StudentAssignmentInfo [uid=" + uid + ", assignKey=" + assignKey + ", dueDate=" + dueDate + ", isGraded=" + isGraded + ", comments=" + comments
                + ", status=" + status + "]";
    }
}

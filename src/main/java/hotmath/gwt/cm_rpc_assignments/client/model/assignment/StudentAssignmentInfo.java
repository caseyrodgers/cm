package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

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
    private String score;
    private int numUnreadAnnotations;
    boolean changedAssignment;

    public int getNumUnreadAnnotations() {
        return numUnreadAnnotations;
    }

    public void setNumUnreadAnnotations(int numUnreadAnnotations) {
        this.numUnreadAnnotations = numUnreadAnnotations;
    }

    public StudentAssignmentInfo() {}
    
    public StudentAssignmentInfo(int assignKey, int uid, boolean graded, Date turnInDate, String status, Date dueDate, String comments, int cntProblems, int cntSubmitted, String score, boolean changedAssignment) {
        this.assignKey = assignKey;
        this.uid = uid;
        this.isGraded = graded;
        this.turnInDate = turnInDate;
        this.status = status;
        this.dueDate = dueDate;
        this.comments = comments;
        this.cntProblems = cntProblems;
        this.cntSubmitted = cntSubmitted;
        this.score = score;   
        this.changedAssignment = changedAssignment;
    }
    
    /** Is there anything interestingly new about this assignment?
     * 
     * @return
     */
    public boolean isChanged() {
        if(numUnreadAnnotations > 0) {
            return true;
        }
        else {
            return changedAssignment;
        }
    }

    public void setChanged(boolean changed) {
        this.changedAssignment = changed;
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

    public String getScore() {
        
        if(isGraded()) {
            return score;
        }
        else {
            return "--";
        }
    }

    public void setScore(String score) {
        this.score = score;
    }
    @Override
    public String toString() {
        return "StudentAssignmentInfo [uid=" + uid + ", assignKey=" + assignKey + ", dueDate=" + dueDate + ", isGraded=" + isGraded + ", comments=" + comments
                + ", status=" + status + ", turnInDate=" + turnInDate + ", cntProblems=" + cntProblems + ", cntSubmitted=" + cntSubmitted + ", score=" + score
                + ", numUnreadAnnotations=" + numUnreadAnnotations + "]";
    }

    public String getLabelForStudent() {
        return truncate(25,getComments()) + ", " + getStatus();
    }
    
    private String truncate(int len, String s) {
        if(s.length() < len) {
            return s;
        }
        else {
            return s.substring(0,22) + " ...";
        }
    }
}

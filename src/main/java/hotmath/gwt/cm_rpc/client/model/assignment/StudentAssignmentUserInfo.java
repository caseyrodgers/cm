package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.Date;

/** Represents a user's state information about individual
 *  assignments.  
 *  
 * @author casey
 *
 */
public class StudentAssignmentUserInfo implements Response {
    
    private int uid;
    private int assignKey;
    private Date turnInDate;
    private boolean graded;

    public StudentAssignmentUserInfo(){}
    
    public StudentAssignmentUserInfo(int uid, int assignKey, Date turnInDate, boolean graded) {
        this.uid = uid;
        this.assignKey = assignKey;
        this.turnInDate = turnInDate;
        this.graded = graded;
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

    public Date getTurnInDate() {
        return turnInDate;
    }

    public void setTurnInDate(Date turnInDate) {
        this.turnInDate = turnInDate;
    }

    public boolean isGraded() {
        return graded;
    }

    public void setGraded(boolean graded) {
        this.graded = graded;
    }
    
}

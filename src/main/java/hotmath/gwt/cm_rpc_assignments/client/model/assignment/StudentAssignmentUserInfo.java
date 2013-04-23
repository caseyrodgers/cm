package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

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
    private Date viewDateTime;
    private boolean graded;

    public StudentAssignmentUserInfo(){}

    public StudentAssignmentUserInfo(int uid, int assignKey) {
        this.uid = uid;
        this.assignKey = assignKey;
    }

    public StudentAssignmentUserInfo(int uid, int assignKey, Date turnInDate, boolean graded, Date viewDateTime) {
        this(uid, assignKey);
        this.turnInDate = turnInDate;
        this.graded = graded;
        this.viewDateTime = viewDateTime;
    }

    public Date getViewDateTime() {
        return viewDateTime;
    }

    public void setViewDateTime(Date viewDateTime) {
        this.viewDateTime = viewDateTime;
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

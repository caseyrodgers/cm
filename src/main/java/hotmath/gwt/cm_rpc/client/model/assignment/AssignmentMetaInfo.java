package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

/** Contains current state meta information about 
 *  a single user's Assignment hierarchy
 * @author casey
 *
 */
public class AssignmentMetaInfo implements Response {
    
    private int activeAssignments;
    private int closedAssignments;
    private int expiredAssignments;
    private int unreadMessages;

    public AssignmentMetaInfo(){}
    
    public AssignmentMetaInfo(int activeAssignments, int closedAssignments, int expiredAssignments, int unreadMessages) {
        this.activeAssignments = activeAssignments;
        this.closedAssignments = closedAssignments;
        this.expiredAssignments = expiredAssignments;
        this.unreadMessages = unreadMessages;
    }

    public int getActiveAssignments() {
        return activeAssignments;
    }

    public void setActiveAssignments(int activeAssignments) {
        this.activeAssignments = activeAssignments;
    }

    public int getClosedAssignments() {
        return closedAssignments;
    }

    public void setClosedAssignments(int closedAssignments) {
        this.closedAssignments = closedAssignments;
    }

    public int getExpiredAssignments() {
        return expiredAssignments;
    }

    public void setExpiredAssignments(int expiredAssignments) {
        this.expiredAssignments = expiredAssignments;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }
}

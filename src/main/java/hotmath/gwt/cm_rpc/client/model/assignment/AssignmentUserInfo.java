package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.List;

/** Contains current state meta information about 
 *  a single user's Assignment hierarchy
 * @author casey
 *
 */
public class AssignmentUserInfo implements Response {

    private int activeAssignments;
    private int closedAssignments;
    private int expiredAssignments;
    private List<ProblemAnnotation> unreadAnnotations;
    private boolean adminUsingAssignments;

    public AssignmentUserInfo(){}
    
    
    public List<ProblemAnnotation> getUnreadAnnotations() {
        return unreadAnnotations;
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

    public boolean isAdminUsingAssignments() {
        return adminUsingAssignments;
    }

    public void setAdminUsingAssignments(boolean adminUsingAssignments) {
        this.adminUsingAssignments = adminUsingAssignments;
    }


    public int getUnreadMessageCount() {
        if(unreadAnnotations == null || unreadAnnotations.size() == 0) {
            return 0;
        }
        else {
            return unreadAnnotations.size();
        }
    }

    public void setUnreadAnnotations(List<ProblemAnnotation> unreadAnnotations) {
        this.unreadAnnotations = unreadAnnotations;
    }

    public boolean hasUnreadAnnotation(int assignKey, String pid) {
        for(ProblemAnnotation pa: unreadAnnotations) {
            if(pa.getAssignKey() == assignKey && pa.getPid().equals(pid)) {
                return true;
            }
        }
        return false;
    }
    
}

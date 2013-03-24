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
    private boolean changed;

    public AssignmentUserInfo(){}
    
    
    public boolean isChanged() {
        return changed;
    }


    public void setChanged(boolean changed) {
        this.changed = changed;
    }


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
    

    /** TODO: move to central place to handle updates the global annotation 
     *        data ... widgets need to be updated depending on state of dynamic
     *        data ... should use event 
     *        
     *        
     *        return true if was removed, false if no change
     *        
     * @param studentProb
     */
    public boolean removeFromUnreadAnnotations(StudentAssignment studentAssignment, StudentProblemDto studentProb) {
        AssignmentUserInfo ami = this;
        int thisAssignKey = studentAssignment.getAssignment().getAssignKey();
        if(ami != null) {
            List<ProblemAnnotation> unread = ami.getUnreadAnnotations();
            for(int which=0;which<unread.size();which++) {
                ProblemAnnotation pa = unread.get(which);
                if(pa.getAssignKey() == thisAssignKey) {
                    if(studentProb.getPid().equals(pa.getPid())) {
                        ami.getUnreadAnnotations().remove(pa);
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AssignmentUserInfo) {
            AssignmentUserInfo o1 = (AssignmentUserInfo)obj;
            
            /** first check basic info
             * 
             */
            if(o1.isChanged() != isChanged()) {
                return false;
            }
            
            if(o1.getActiveAssignments() != getActiveAssignments()) {
                return false;
            }
            
            if(o1.getClosedAssignments() != getClosedAssignments()) {
                return false;
            }
            
            if(o1.getExpiredAssignments() != getExpiredAssignments()) {
                return false;
            }

            List<ProblemAnnotation> ura = o1.getUnreadAnnotations();
            if(ura.size() != getUnreadAnnotations().size()) {
                return false;
            }

            for(ProblemAnnotation pa1: ura) {
                boolean found=false;
                for(ProblemAnnotation pa2: getUnreadAnnotations()) {
                    if(pa2.getPid().equals(pa1.getPid()) && pa2.getAssignKey() == pa1.getAssignKey()) {
                        found=true;
                        break;
                    }
                }
                
                if(!found) {
                    return false;
                }
            }
            
            return true;  // all good!
        }
        else {
            return super.equals(obj);
        }
    }
}

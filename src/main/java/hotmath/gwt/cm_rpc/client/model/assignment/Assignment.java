package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/** Define a single assignment as defined in CM.
 * 
 * A assignment is:
 * 
 * 1. a list of PIDS that represent the homework problems
 * 2. a due date when the assignment is to be completed
 * 3. a list of UIDS that represent the users of the assignment.
 * 
 * 
 * @author casey
 *
 */
public class Assignment implements Response{

    String assignmentName;
    int assignKey;
    int groupId;
    String comments;
    Date dueDate;
    CmList<ProblemDto> pids = new CmArrayList<ProblemDto>();
    List<Integer> uids = new ArrayList<Integer>();
    String status;
    boolean draftMode;
    
    // might be null indicate not set
    Integer problemCount;
    
    public Assignment() {}
    
    public Assignment(boolean draftMode, int assignKey, int groupId, String name, String comments, Date dueDate, CmList<ProblemDto> pids, List<Integer> uids, String status) {
        this.draftMode = draftMode;
        this.assignKey = assignKey;
        this.groupId = groupId;
        this.assignmentName = name;
        this.comments = comments;
        this.dueDate = dueDate;
        this.pids = pids;
        this.uids = uids;
        this.status = status;
    }
    
    public boolean isDraftMode() {
        return draftMode;
    }

    public void setDraftMode(boolean draftMode) {
        this.draftMode = draftMode;
    }

    public String getStatusLabel() {
        if(isExpired()) {
            return "Expired";
        }
        else {
            return getStatus();
        }
    }
    
    public String getAssignmentLabel() {
        String label = getAssignmentName();
        if(isExpired()) {
            label += " (Expired)";
        }
        else if (!isEditable()) {
            label += " (Closed)";
        }
        
        return label;
    }
    
    /** Is this assignment editable at all
     * 
     * @return
     */
    public boolean isEditable() {
        return !status.equalsIgnoreCase("closed");
    }
    
    /** Determine, based on data
     * in object if this assignment 
     * has expired.
     * 
     * Expires the day after the expiration date (inclusive)
     * 
     * @return
     */
    public boolean isExpired() {
        int millsInDay = 86400000;
        return dueDate.getTime() < (System.currentTimeMillis() - millsInDay);
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public CmList<ProblemDto> getPids() {
        return pids;
    }

    public void setPids(CmList<ProblemDto> pids) {
        this.pids = pids;
    }

    public List<Integer> getUids() {
        return uids;
    }

    public void setUids(List<Integer> uids) {
        this.uids = uids;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }
    

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getProblemCount() {
        return problemCount;
    }

    public void setProblemCount(Integer problemCount) {
        this.problemCount = problemCount;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }   
    
    @Override
    public String toString() {
        return "Assignment [assignmentName=" + assignmentName + ", assignKey=" + assignKey + ", groupId=" + groupId
                + ", comments=" + comments + ", dueDate=" + dueDate + ", pids=" + pids + ", uids=" + uids + ", status="
                + status + ", problemCount=" + problemCount + "]";
    }
}

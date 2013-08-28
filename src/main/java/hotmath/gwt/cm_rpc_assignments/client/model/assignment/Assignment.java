package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.Date;


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

    int adminId;
    String assignmentName;
    int assignKey;
    int groupId;
    String comments;
    Date dueDate;
    CmList<ProblemDto> pids = new CmArrayList<ProblemDto>();
    String status;
    boolean graded;
    Date modifiedTime;
    
    // might be null indicate not set
    Integer problemCount;
    
    // piggy back along with RPC data.  
    // TODO: does this belong here?
    AssignmentGradeDetailInfo gradedInfo;
    
    boolean autoRelease;
    private boolean allowPastDueSubmits;
    boolean personalized;
    boolean preventLessonAccess;

    public Assignment() {}
    
    public Assignment(int adminId, int assignKey, int groupId, String name, String comments, Date dueDate, CmList<ProblemDto> pids, String status, boolean allowPastDueSubmits, boolean graded, Date modifiedTime,boolean autoRelease, boolean isPersonalized,boolean preventLessonAccess) {
        this.adminId = adminId;
        this.assignKey = assignKey;
        this.groupId = groupId;
        this.assignmentName = name;
        this.comments = comments;
        this.dueDate = dueDate;
        this.pids = pids;
        this.status = status;
        this.allowPastDueSubmits = allowPastDueSubmits;
        this.graded = graded;
        this.modifiedTime = modifiedTime;
        this.autoRelease = autoRelease;
        this.personalized = isPersonalized;
        this.preventLessonAccess = preventLessonAccess;
    }


    public boolean isPreventLessonAccess() {
        return preventLessonAccess;
    }

    public void setPreventLessonAccess(boolean preventLessonAccess) {
        this.preventLessonAccess = preventLessonAccess;
    }

    public boolean isPersonalized() {
        return personalized;
    }

    public void setPersonalized(boolean personalized) {
        this.personalized = personalized;
    }

    public boolean isAllowPastDueSubmits() {
        return allowPastDueSubmits;
    }

    public void setAllowPastDueSubmits(boolean allowPastDueSubmits) {
        this.allowPastDueSubmits = allowPastDueSubmits;
    }

    public boolean isAutoRelease() {
        return autoRelease;
    }

    public void setAutoRelease(boolean autoRelease) {
        this.autoRelease = autoRelease;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public int getAdminId() {
        return adminId;
    }
    
    public boolean isClosed() {
        return status.equals("Closed");
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getStatusLabel() {
        return getStatus();
    }


    public String getAssignmentLabel() {
        String label = getComments();
        
        if (!isEditable()) {
            label += " (Closed)";
        }
        else {
            if(isExpired()) {
                label += " (Past Due";
            }
            else {
                label += " (" + getStatus();
            }
            
            label +=  ", Due: " + getDueDate() + ")";;
        }
        return label;
    }
    
    /** Is this assignment editable at all
     * 
    * 
     * @return
     */
    public boolean isEditable() {
        return !getStatus().equals("Closed");
    }

    /** Determine, based on data
     * in object if this assignment 
     * has expired.
     * 
     * Expires the day after the expiration date (inclusive)
     * 
     * @return
     */
    public static final int MILLS_IN_DAY = 24 * 60 * 60 * 1000;

    static public boolean isExpired(Date date) {
        if(date != null) {
            return date.getTime() < (System.currentTimeMillis() - MILLS_IN_DAY);
        }
        else {
            return false;
        }
    }
    
    public boolean isExpired() {
        return isExpired(dueDate);
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

    public boolean isGraded() {
        return graded;
    }

    public void setGraded(boolean graded) {
        this.graded = graded;
    }

    
    public AssignmentGradeDetailInfo getGradedInfo() {
        return gradedInfo;
    }

    public void setGradedInfo(AssignmentGradeDetailInfo gradedInfo) {
        this.gradedInfo = gradedInfo;
    }

    @Override
    public String toString() {
        return "Assignment [adminId=" + adminId + ", assignmentName=" + assignmentName + ", assignKey=" + assignKey + ", groupId=" + groupId + ", comments="
                + comments + ", dueDate=" + dueDate + ", pids=" + pids + ", status=" + status + ", graded=" + graded + ", modifiedTime=" + modifiedTime
                + ", problemCount=" + problemCount + ", gradedInfo=" + gradedInfo + ", autoRelease=" + autoRelease + ", allowPastDueSubmits="
                + allowPastDueSubmits + "]";
    }
}

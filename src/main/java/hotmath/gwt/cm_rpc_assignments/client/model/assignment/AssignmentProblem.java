package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.Date;


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
    private StudentProblemDto studentProblem;
    private boolean isGraded;
    private String status;
    private boolean assignmentClosed;
    private Date assignmentDueDate;
    private boolean allowPastDueSubmits;

    public AssignmentProblem(){}
    
    public AssignmentProblem(int userId, int assignKey, boolean isAssignmentGraded,boolean isAssignmentClosed, SolutionInfo info, StudentProblemDto stuProblem,  String lastUserWidgetValue, String status, Date assignmentDueDate, boolean allowPastDueSubmits) {
        this.userId = userId;
        this.assignKey = assignKey;
        this.isGraded = isAssignmentGraded;
        this.assignmentClosed = isAssignmentClosed;
        this.info = info;
        this.studentProblem = stuProblem;
        this.problemType = stuProblem.getProblem().getProblemType();
        this.lastUserWidgetValue = lastUserWidgetValue;
        this.status = status;
        this.assignmentDueDate = assignmentDueDate;
        this.allowPastDueSubmits = allowPastDueSubmits;
    }
    
    
    /** Is this assignment pastdue.  This takes into 
     * account if the allowPastDueSubmits is enabled.
     * 
     * if allowPastDueSubmits is true, then the assignment 
     * will never be pastDue.
     *  
     * 
     * @return
     */
    public boolean isPastDue() {
        if(assignmentDueDate == null) {
            return false;
        }
        else {
            boolean pastDue = Assignment.isExpired(assignmentDueDate);
            if(pastDue && !allowPastDueSubmits) {
                return true;   // assignment is past due
            }
            else {
                return false;  // assignment submits allowed
            }
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public StudentProblemDto getStudentProblem() {
        return studentProblem;
    }

    public void setStudentProblem(StudentProblemDto studentProblem) {
        this.studentProblem = studentProblem;
    }

    public boolean isGraded() {
        return isGraded;
    }

    public void setGraded(boolean isGraded) {
        this.isGraded = isGraded;
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

    public boolean isAssignmentClosed() {
        return assignmentClosed;
    }

    public void setAssignmentClosed(boolean assignmentClosed) {
        this.assignmentClosed = assignmentClosed;
    }

    public Date getAssignmentDueDate() {
        return assignmentDueDate;
    }

    /** Can input values be saved to named assignment?
     * 
     * Returns error string if value cannot be saved. Otherwise, null is returned.
     * 
     * @param assProblem
     * @return
     */
    public static String canInputValueBeSaved(AssignmentProblem assProblem) {
        if(assProblem.isGraded()) {
            return "This input value will not be saved because the assignment has already been graded.";
        }

        if(assProblem.isAssignmentClosed()) {
            return "This input value will not be saved because the assignment is closed.";
        }
        
        if(assProblem.isPastDue()) {
            return "This input value will not be saved because the assignment is past due.";
        }
        
        return null;
    }
    
}

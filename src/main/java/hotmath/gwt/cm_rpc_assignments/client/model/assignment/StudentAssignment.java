package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

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
public class StudentAssignment implements Response {
    
    private int uid;
    private Assignment assignment;
    private String studentName;
    private String homeworkStatus;
    private String homeworkGrade;

    private int problemCount;
    private int problemCompletedCount;
    private int problemPendingCount;
    
    private String studentDetailStatus;
    
    boolean isComplete;
    
    private boolean assignmentGraded;
    private Date turnInDate;
    
    
   StudentAssignmentStatuses studentStatuses;

    public StudentAssignment(){}
    
    public StudentAssignment(int uid, Assignment assignment, Date turnInDate, boolean assignmentGraded) {
        this.uid = uid;
        this.assignment = assignment;
        this.turnInDate = turnInDate;
        this.assignmentGraded = assignmentGraded;
    }

    public Date getTurnInDate() {
        return turnInDate;
    }

    public void setTurnInDate(Date turnInDate) {
        this.turnInDate = turnInDate;
    }


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

    public String getHomeworkStatus() {
		return homeworkStatus;
	}

	public void setHomeworkStatus(String homeworkStatus) {
		this.homeworkStatus = homeworkStatus;
	}

	public String getHomeworkGrade() {
	    
	    /** If this assignment is graded and open, then the score should be shown
	     * 
	     */
	    if(this.assignment.getStatus().equals("Open") && isGraded()) {
	        if(homeworkGrade == null || homeworkGrade.equals("-")) {
	            homeworkGrade = "0%";
	        }
	    }
		return homeworkGrade;
	}

	public void setHomeworkGrade(String homeworkGrade) {
		this.homeworkGrade = homeworkGrade;
	}

    public int getProblemCount() {
		return problemCount;
	}

	public void setProblemCount(int problemCount) {
		this.problemCount = problemCount;
	}

	public int getProblemCompletedCount() {
		return problemCompletedCount;
	}

	public void setProblemCompletedCount(int problemCompletedCount) {
		this.problemCompletedCount = problemCompletedCount;
	}

	public int getProblemPendingCount() {
		return problemPendingCount;
	}

	public void setProblemPendingCount(int problemPendingCount) {
		this.problemPendingCount = problemPendingCount;
	}

	/** Return overall status for this
     *  user's instance of this Assignment 
     *  
     *  
     *  
     * @return
     */
    public String getStatus() {
        return assignment.getStatus();
    }


    public String getStudentDetailStatus() {
        return studentDetailStatus;
    }

    public void setStudentDetailStatus(String studentDetailStatus) {
        this.studentDetailStatus = studentDetailStatus;
    }

    public boolean isGraded() {
        return assignmentGraded;
    }

    public void setGraded(boolean graded) {
        this.assignmentGraded = graded;
    }
	
    public boolean isTurnedIn() {
        return this.turnInDate != null;
    }

    /** Is this student assignment editable?
     * 
     * @return
     */
    public boolean isEditable() {
        if(!getAssignment().isEditable()) {
           return false;   
        }
        else if(!isGraded()) {
            return true;
        }
        else {
            return false;
        }
    }
    

    public StudentAssignmentStatuses getStudentStatuses() {
        return studentStatuses;
    }

    public void setStudentStatuses(StudentAssignmentStatuses studentStatuses) {
        this.studentStatuses = studentStatuses;
    }
}

package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;

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
public class StudentAssignment implements Response {
    
    private int uid;
    private Assignment assignment;
    private String studentName;
    private String homeworkStatus;
    private String homeworkGrade;
    private CmList<StudentProblemDto> assigmentStatuses;
    private CmList<StudentLessonDto> lessonStatuses;

    private int problemCount;
    private int problemCompletedCount;
    private int problemPendingCount;
    
    private String studentDetailStatus;
    
    boolean isComplete;


    public StudentAssignment(){}
    
    public StudentAssignment(int uid, Assignment assignment, CmList<StudentProblemDto> assignmentStatuses) {
        this.uid = uid;
        this.assignment = assignment;
        this.assigmentStatuses = assignmentStatuses;
    }

    /** Return true if this Assignment is 
     *  considered complete from the student's
     *  point of view.
     *  
     *  
     * @return
     */
    public boolean isComplete() {
        for(StudentProblemDto spd: assigmentStatuses) {
            if(!spd.isComplete()) {
                return false;
            }
        }
        return true;
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
		return homeworkGrade;
	}

	public void setHomeworkGrade(String homeworkGrade) {
		this.homeworkGrade = homeworkGrade;
	}

	public List<StudentProblemDto> getAssigmentStatuses() {
        return assigmentStatuses;
    }

    public void setAssigmentStatuses(CmList<StudentProblemDto> assigmentStatuses) {
        this.assigmentStatuses = assigmentStatuses;
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

	@Override
    public String toString() {
        return "StudentAssignment [uid=" + uid + ", assignment=" + assignment + ", assigmentStatuses="
                + assigmentStatuses + "]";
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

	public List<StudentLessonDto> getLessonStatuses() {
		return lessonStatuses;
	}

	public void setLessonStatuses(CmList<StudentLessonDto> lessonList) {
		lessonStatuses = lessonList;
	}

    public String getStudentDetailStatus() {
        return studentDetailStatus;
    }

    public void setStudentDetailStatus(String studentDetailStatus) {
        this.studentDetailStatus = studentDetailStatus;
    }
	
	
}

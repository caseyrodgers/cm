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
    private CmList<StudentProblemDto> assigmentStatuses;

    public StudentAssignment(){}
    
    public StudentAssignment(int uid, Assignment assignment, CmList<StudentProblemDto> assignmentStatuses) {
        this.uid = uid;
        this.assignment = assignment;
        this.assigmentStatuses = assignmentStatuses;
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

	public List<StudentProblemDto> getAssigmentStatuses() {
        return assigmentStatuses;
    }

    public void setAssigmentStatuses(CmList<StudentProblemDto> assigmentStatuses) {
        this.assigmentStatuses = assigmentStatuses;
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
}

package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class StudentProblemDto implements Response {

    private int uid;
    private ProblemDto problem;
    private String basePid;  // pid that points the pid choosen by admin
    private String status;
	private String gradeStatus;
    private boolean hasShowWork;
    private boolean hasShowWorkAdmin;
    private boolean assignmentClosed;
    private boolean graded;
    private boolean assignmentGraded;
    
    

    public StudentProblemDto() {
    }

    public StudentProblemDto(int uid, ProblemDto problem, String status, boolean hasShowWork, boolean hasShowWorkAdmin, boolean isAssignmentClosed, boolean isAssignmentGraded, boolean graded) {
        this.uid = uid;
        this.problem = problem;
        this.status = status;
        this.hasShowWork = hasShowWork;
        this.hasShowWorkAdmin = hasShowWorkAdmin;
        this.assignmentClosed = isAssignmentClosed;
        this.assignmentGraded = isAssignmentGraded;
        this.graded = graded;
    }

    public StudentProblemDto(int uid, ProblemDto problem, String status, String gradeStatus, boolean hasShowWork, boolean hasShowWorkAdmin, boolean isAssignmentClosed, boolean isAssignmentGraded, boolean graded) {
        this.uid = uid;
        this.problem = problem;
        this.status = status;
        this.gradeStatus = gradeStatus;
        this.hasShowWork = hasShowWork;
        this.hasShowWorkAdmin = hasShowWorkAdmin;
        this.assignmentClosed = isAssignmentClosed;
        this.assignmentGraded = isAssignmentGraded;
        this.graded = graded;
    }

    public String getBasePid() {
        return basePid;
    }

    public void setBasePid(String basePid) {
        this.basePid = basePid;
    }

    public boolean isHasShowWorkAdmin() {
        return hasShowWorkAdmin;
    }

    public void setHasShowWorkAdmin(boolean hasShowWorkAdmin) {
        this.hasShowWorkAdmin = hasShowWorkAdmin;
    }

    public boolean isHasShowWork() {
        return hasShowWork;
    }

    public void setHasShowWork(boolean hasShowWork) {
        this.hasShowWork = hasShowWork;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public ProblemDto getProblem() {
        return problem;
    }

    public void setProblem(ProblemDto problem) {
        this.problem = problem;
    }
    
    public String getStatus() {
        if(status == null || status.length() == 0) {
            return "Not Answered";
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getGradeStatus() {
		return gradeStatus;
	}

	public void setGradeStatus(String gradeStatus) {
		this.gradeStatus = gradeStatus;
	}

	/** Return the label shown as the status
     * for this problem to the student.
     * 
     * Viewed/Not viewed is simply unanswered
     * 
     * @return
     */
    public String getStatusForStudent() {
       String sl = getStatus().toLowerCase();

       if(sl.contains("viewed")) {
           // viewed, not viewed
           return "Not Answered";
       }
       else if(assignmentGraded) {
           return status;
       }
       else if(sl.equals("correct") || sl.equals("incorrect") || sl.equals("half credit")) {
           return "Submitted";
       }
       else {
           return status;
       }
    }
    
	/** return student ready label
	 * 
	 * @return
	 */
	public String getPidLabel() {
        return removeProblemType(getProblem().getLabel());
    }
    
    public String getPid() {
        return problem.getPid();
    }
    
    public boolean isCorrect() {
        return status != null && status.equalsIgnoreCase("correct");
    }

	/** Return true if this student problem is considered 'complete'. 
	 * 
	 * Complete means the user has done something with the problem, either
	 * answered the widget or drawn on the whiteboard.
	 * 
	 * @return
	 */
    public boolean isComplete() {
        String s = status.toLowerCase();
        if(s.contains("viewed")) {   // viewed and not viewed
            return false;
        }
        else {
            // all others?
            return true;
        }
    }

    public int getProblemNumberOrdinal() {
        return problem.getOrdinalNumber();
    }
    
    public String getStudentLabel() {
        return "Problem " + getProblem().getOrdinalNumber(); // + ". " + removeProblemType(getProblem().getLabel());
    }

    /** Remove the ': TYPE' label on pid problems
     * 
     * @param fromLabel
     * @return
     */
    public static String removeProblemType(String fromLabel) {
        for(int i=fromLabel.length();i>0;i--) {
            if(fromLabel.charAt(i-1) == ':') {
                fromLabel = fromLabel.substring(0, i-1);
                break;
            }
        }
        return fromLabel;
    }

    public boolean isGraded() {
        return graded || assignmentGraded;
    }
    
    
    public void setGraded(boolean graded) {
        this.graded = graded;
    }

    public String getStudentLabelWithStatus() {
        String label = getStudentLabel();
        String status = getStatusForStudent();
        
        String html=label + "<div style='float: right;margin-right: 15px;font-size: .8em;color: gray'>" + status + "</div>";
        if(isComplete()) {
            html = "<span style='color: gray'>" + html + "</html>";
        }
        return html;
    }


    @Override
    public String toString() {
        return "StudentProblemDto [uid=" + uid + ", problem=" + problem + ", basePid=" + basePid + ", status=" + status + ", hasShowWork=" + hasShowWork
                + ", hasShowWorkAdmin=" + hasShowWorkAdmin + ", assignmentClosed=" + assignmentClosed + ", graded=" + graded + ", assignmentGraded="
                + assignmentGraded + "]";
    }

}

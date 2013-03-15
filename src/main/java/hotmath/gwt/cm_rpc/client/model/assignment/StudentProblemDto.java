package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class StudentProblemDto implements Response {

    private int uid;
    private ProblemDto problem;
    private String status;
    private boolean hasShowWork;
    private boolean hasShowWorkAdmin;
    private boolean isAssignmentClosed;
    private boolean graded;

    public StudentProblemDto() {
    }

    public StudentProblemDto(int uid, ProblemDto problem, String status, boolean hasShowWork, boolean hasShowWorkAdmin, boolean isAssignmentClosed, boolean graded) {
        this.uid = uid;
        this.problem = problem;
        this.status = status;
        this.hasShowWork = hasShowWork;
        this.hasShowWorkAdmin = hasShowWorkAdmin;
        this.isAssignmentClosed = isAssignmentClosed;
        this.graded = graded;
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
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
       
       if(isAssignmentClosed) {
           return status;
       }
       else if(sl.contains("viewed")) {
           // viewed, not viewed
           return "Not Answered";
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
        return getStudentLabel(getProblem().getLabel());
    }
    
    public String getPid() {
        return problem.getPid();
    }
    
    public boolean isCorrect() {
        return status != null && status.equalsIgnoreCase("correct");
    }

	@Override
    public String toString() {
        return "StudentProblemDto [uid=" + uid + ", problem=" + problem + ", status=" + status + "]";
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

    public static String getStudentLabel(String fromLabel) {
        for(int i=fromLabel.length();i>0;i--) {
            if(fromLabel.charAt(i-1) == ':') {
                fromLabel = fromLabel.substring(0, i-1);
                break;
            }
        }
        return fromLabel;
    }

    public boolean isGraded() {
        return graded;
    }
    
    
    public void setGraded(boolean graded) {
        this.graded = graded;
    }
}

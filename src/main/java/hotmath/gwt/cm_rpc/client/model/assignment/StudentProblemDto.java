package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class StudentProblemDto implements Response {

    private int uid;
    private ProblemDto problem;
    private String status;
    private String isGraded;
    private boolean hasShowWork;
    private boolean hasShowWorkAdmin;
    private boolean isClosed;
    
    public StudentProblemDto() {
    }

    public StudentProblemDto(int uid, ProblemDto problem, String status, boolean hasShowWork, boolean hasShowWorkAdmin, boolean isClosed) {
        this.uid = uid;
        this.problem = problem;
        this.status = status;
        this.hasShowWork = hasShowWork;
        this.hasShowWorkAdmin = hasShowWorkAdmin;
        this.isClosed = isClosed;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean isClosed) {
        this.isClosed = isClosed;
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
       
       if(isClosed) {
           return status;
       }
       else if(sl.contains("viewed")) {
           // viewed, not viewed
           return "Not Answered";
       }
       else if(sl.equals("correct") || sl.equals("incorrect")) {
           return "Submitted";
       }
       else {
           return status;
       }
    }

    public String getIsGraded() {
		return isGraded;
	}

	public void setIsGraded(String isGraded) {
		this.isGraded = isGraded;
	}

	public String getPidLabel() {
        return getProblem().getLabel();
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
}

package hotmath.cm.server.model;

public class StudentAssignmentStatus {

    String studentName;
	int    userId;
    int    score;
    int    assignKey;
    int    numCorrect;
    int    numHalfCredit;
    int    numTotal;

    public StudentAssignmentStatus() {}
    
    public StudentAssignmentStatus(String studentName, int userId, int score, int assignKey, int numCorrect,
    		int numHalfCredit, int numTotal) {
        this.studentName = studentName;
    	this.userId = userId;
        this.score = score;
        this.assignKey = assignKey;
        this.numCorrect = numCorrect;
        this.numHalfCredit = numHalfCredit;
        this.numTotal = numTotal;
    }
    
    public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getAssignKey() {
		return assignKey;
	}
	public void setAssignKey(int assignKey) {
		this.assignKey = assignKey;
	}
	public int getNumCorrect() {
		return numCorrect;
	}
	public void setNumCorrect(int numCorrect) {
		this.numCorrect = numCorrect;
	}
	public int getNumHalfCredit() {
		return numHalfCredit;
	}
	public void setNumHalfCredit(int numHalfCredit) {
		this.numHalfCredit = numHalfCredit;
	}
	public int getNumTotal() {
		return numTotal;
	}
	public void setNumTotal(int numTotal) {
		this.numTotal = numTotal;
	}
}

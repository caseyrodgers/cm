package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class CustomQuizDef implements Response {
    
    int quizId;
    String quizName;
    int adminId;
    boolean isAnswersViewable;
    boolean isArchived;
    boolean isInUse;
    String archiveDate;
    
    public CustomQuizDef() {
    }
    public CustomQuizDef(int quizId, String quizName, int adminId, boolean isAnswersViewable,
    		boolean isInUse, boolean isArchived, String archiveDate) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.adminId = adminId;
        this.isAnswersViewable = isAnswersViewable;
        this.isInUse = isInUse;
        this.isArchived = isArchived;
        this.archiveDate = archiveDate;
    }
    public String getQuizName() {
        return quizName;
    }
    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }
    public int getAdminId() {
        return adminId;
    }
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    public int getQuizId() {
        return quizId;
    }
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
	public boolean isAnswersViewable() {
		return isAnswersViewable;
	}
	public void setAnswersViewable(boolean isAnswersViewable) {
		this.isAnswersViewable = isAnswersViewable;
	}
	public boolean isArchived() {
		return isArchived;
	}
	public void setArchived(boolean isArchived) {
		this.isArchived = isArchived;
	}
	public boolean isInUse() {
		return isInUse;
	}
	public void setInUse(boolean isInUse) {
		this.isInUse = isInUse;
	}
	public String getArchiveDate() {
		return archiveDate;
	}
	public void setArchiveDate(String archiveDate) {
		this.archiveDate = archiveDate;
	}
    
}

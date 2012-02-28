package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

public class ArchiveCustomQuizAction implements Action<RpcData> {
    
    int adminId;
    int quizId;
    
    public ArchiveCustomQuizAction(){}
    
    public ArchiveCustomQuizAction(int adminId, int quizId) {
        this.adminId = adminId;
        this.quizId = quizId;
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

	@Override
    public String toString() {
        return "ArchiveCustomQuizAction [adminId=" + adminId + ", quizid=" + quizId + "]";
    }
}
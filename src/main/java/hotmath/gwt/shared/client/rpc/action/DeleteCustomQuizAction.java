package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

public class DeleteCustomQuizAction implements Action<RpcData> {
    
    int adminId;
    String name;
    int quizId;
    
    public DeleteCustomQuizAction(){}
    
    public DeleteCustomQuizAction(int adminId, int quizId) {
        this.adminId = adminId;
        this.quizId = quizId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	@Override
    public String toString() {
        return "DeleteCustomQuizAction [adminId=" + adminId + ", quizId=" + quizId + "]";
    }
}
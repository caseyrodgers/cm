package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.CustomQuizId;

import java.util.List;

public class SaveCustomQuizAction implements Action<RpcData>{
    
    int adminId;
    String cqName;
    List<CustomQuizId> ids;
    CustomQuizDef customQuiz;
    
    public SaveCustomQuizAction(){}
    
    public SaveCustomQuizAction(int adminId, String cqName, List<CustomQuizId> ids) {
        this.adminId = adminId;
        this.cqName = cqName;
        this.ids = ids;
    }

    public SaveCustomQuizAction(CustomQuizDef customQuiz, List<CustomQuizId> ids) {
        this.adminId = customQuiz.getAdminId();
        this.cqName = customQuiz.getQuizName();
        this.customQuiz = customQuiz;
        this.ids = ids;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getCpName() {
        return cqName;
    }

    public void setCpName(String cpName) {
        this.cqName = cpName;
    }

    public List<CustomQuizId> getIds() {
        return ids;
    }

    public void setIds(List<CustomQuizId> ids) {
        this.ids = ids;
    }

    public CustomQuizDef getCustomQuiz() {
		return customQuiz;
	}

	public void setCustomQuiz(CustomQuizDef customQuiz) {
		this.customQuiz = customQuiz;
	}

	@Override
    public String toString() {
        return "SaveCustomQuizAction [adminId=" + adminId + ", cqName=" + cqName + 
            ", isAnswersViewable=" + customQuiz.isAnswersViewable() + ", ids=" + ids + "]";
    }
}

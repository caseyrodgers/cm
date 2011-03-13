package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;

public class GetCustomQuizAction implements Action<CmList<QuizQuestion>> {
    
    int adminId;
    int customQuizId;
    
    public GetCustomQuizAction(){}
    
    public GetCustomQuizAction(int customQuizId) {
        this.customQuizId=customQuizId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getCustomQuizId() {
        return customQuizId;
    }

    public void setCustomQuizId(int customQuizId) {
        this.customQuizId = customQuizId;
    }

    @Override
    public String toString() {
        return "GetCustomQuizAction [adminId=" + adminId + ", customQuizId=" + customQuizId + "]";
    }
}

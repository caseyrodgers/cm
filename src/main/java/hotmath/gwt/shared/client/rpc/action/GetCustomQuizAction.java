package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;

public class GetCustomQuizAction implements Action<CmList<QuizQuestion>> {
    
    int adminId;
    String name;
    
    public GetCustomQuizAction(){}
    
    public GetCustomQuizAction(int adminId, String cpName) {
        this.adminId = adminId;
        this.name = cpName;
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

    @Override
    public String toString() {
        return "GetCustomQuizAction [adminId=" + adminId + ", name=" + name + "]";
    }
}

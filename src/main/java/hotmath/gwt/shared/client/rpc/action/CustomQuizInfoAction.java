package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.CustomQuizInfoModel;

public class CustomQuizInfoAction implements Action<CustomQuizInfoModel>{
    CustomQuizDef quiz;
    Integer adminId;
    
    public CustomQuizInfoAction(){
    }

    public CustomQuizInfoAction(Integer adminId, CustomQuizDef quiz) {
        this.adminId = adminId;
        this.quiz = quiz;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public CustomQuizDef getQuiz() {
        return quiz;
    }

    public void setQuiz(CustomQuizDef quiz) {
        this.quiz = quiz;
    }

    @Override
    public String toString() {
        return "CustomQuizInfoAction [quiz=" + quiz + ", adminId=" + adminId + "]";
    }
}    

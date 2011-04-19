package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.shared.client.model.CustomQuizInfoModel;

public class CustomQuizInfoAction implements Action<CustomQuizInfoModel>{
    CustomLessonModel quiz;
    Integer adminId;
    
    public CustomQuizInfoAction(){
    }

    public CustomQuizInfoAction(Integer adminId, CustomLessonModel quiz) {
        this.adminId = adminId;
        this.quiz = quiz;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public CustomLessonModel getQuiz() {
        return quiz;
    }

    public void setQuiz(CustomLessonModel quiz) {
        this.quiz = quiz;
    }

    @Override
    public String toString() {
        return "CustomQuizInfoAction [quiz=" + quiz + ", adminId=" + adminId + "]";
    }
}    

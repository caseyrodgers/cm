package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.model.IntValueHolder;

public class CustomQuizUsageCountAction implements Action<IntValueHolder>{
    Integer quizId;
    
    public CustomQuizUsageCountAction(){
    }

    public CustomQuizUsageCountAction(Integer quizId) {
        this.quizId = quizId;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    @Override
    public String toString() {
        return "CustomQuizUsageCountAction [quizId=" + quizId + "]";
    }
}    

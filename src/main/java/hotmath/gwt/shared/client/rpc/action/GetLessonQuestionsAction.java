package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;

public class GetLessonQuestionsAction implements Action<CmList<QuizQuestion>>{
    
    String lesson;
    
    public GetLessonQuestionsAction(){}
    
    public GetLessonQuestionsAction(String lesson) {
        this.lesson = lesson;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    @Override
    public String toString() {
        return "GetLessonQuestionsAction [lesson=" + lesson + "]";
    }
}

package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;

public class GetLessonQuestionsAction implements Action<CmList<QuizQuestion>>{
    
    String lesson;
    String subject;
    
    public GetLessonQuestionsAction(){}
    
    public GetLessonQuestionsAction(String lesson, String subject) {
        this.lesson = lesson;
        this.subject = subject;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "GetLessonQuestionsAction [lesson=" + lesson + ", subject=" + subject + "]";
    }
}

package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class QuizQuestion implements Response {

    String lesson;
    String quizHtml;
    
    public QuizQuestion() {}
    
    public QuizQuestion(String lesson, String quizHtml) {
        this.lesson = lesson;
        this.quizHtml = quizHtml;
    }
    
    public String getLesson() {
        return lesson;
    }
    public void setLesson(String lesson) {
        this.lesson = lesson;
    }
    public String getQuizHtml() {
        return quizHtml;
    }
    public void setQuizHtml(String quizHtml) {
        this.quizHtml = quizHtml;
    }
}

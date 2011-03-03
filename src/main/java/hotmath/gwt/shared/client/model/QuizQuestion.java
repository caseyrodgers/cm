package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class QuizQuestion implements Response {

    String lesson;
    String programName;
    String questionId;
    String quizHtml;
    String pid;
    
    public QuizQuestion() {}
    
    public QuizQuestion(String questionId, String lesson, String programName,String pid, String quizHtml) {
        this.questionId = questionId;
        this.lesson = lesson;
        this.programName = programName;
        this.pid = pid;
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

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}

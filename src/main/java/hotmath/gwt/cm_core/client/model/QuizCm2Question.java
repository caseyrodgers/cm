package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** Represents a single question of a Cm2Test object
 * 
 * @author casey
 *
 */
public class QuizCm2Question implements Response {
    
    private int quizId;
    private String html;

    public QuizCm2Question() {}
    
    public QuizCm2Question(int quizId, String html) {
        this.quizId = quizId;
        this.html = html;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
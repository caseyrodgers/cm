package hotmath.gwt.cm_activity.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;


/** Represents a single work problem definition
 * 
 * @author casey
 *
 */
public class WordProblem implements Response{
    String question;
    String answer;
    String explanation;
    String vars;
    
    public WordProblem() {
    }
    
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String questionHtml) {
        this.question = questionHtml;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }


    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getVars() {
        return vars;
    }

    public void setVars(String vars) {
        this.vars = vars;
    }
}

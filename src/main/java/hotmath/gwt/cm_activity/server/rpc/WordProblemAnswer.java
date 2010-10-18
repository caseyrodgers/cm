package hotmath.gwt.cm_activity.server.rpc;

public class WordProblemAnswer {
    String text;
    String vars;
    
    public WordProblemAnswer() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
   

    public String getVars() {
        return vars;
    }

    public void setVars(String vars) {
        this.vars = vars;
    }

}

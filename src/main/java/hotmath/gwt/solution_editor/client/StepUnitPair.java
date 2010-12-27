package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_rpc.client.rpc.Response;

/** A complete 'step', consisting of both 
 * a hint and the step text.
 * 
 * @author casey
 *
 */
public class StepUnitPair implements Response {
    String hint;
    String text;

    public StepUnitPair() {}
    
    public StepUnitPair(String hint, String text) {
        this.hint = hint;
        this.text = text;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

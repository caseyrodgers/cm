package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** A complete 'step', consisting of both 
 * a hint and the step text.
 * 
 * @author casey
 *
 */
public class StepUnitPair implements Response {
    String hint;
    String text;
    String figure;

    public StepUnitPair() {}
    
    public StepUnitPair(String hint, String text, String figure) {
        this.hint = hint;
        this.text = text;
        this.figure = figure;
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

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }
    
    @Override
    public String toString() {
        return hint + " " + text + " ";
    }
}

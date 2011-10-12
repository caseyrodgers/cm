package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Response;

/** A complete Step Unit.  Consisting of both
 * a hint-unit and a step-unit.
 * 
 * @author casey
 *
 */
public class SolutionMetaStep implements Response {
    String hint;
    String text;
    String figure;
    SolutionMeta parent;
    
    public SolutionMetaStep() {
        /** for RPC */
    }
    public SolutionMetaStep(SolutionMeta parent) {
        this.parent = parent;
    }
    
    public SolutionMetaStep(SolutionMeta parent,String hint, String text, String figure) {
        this.parent = parent;
        this.hint = hint;
        this.text = text;
        this.figure = figure;
    }

    public SolutionMeta getParent() {
        return parent;
    }

    public void setParent(SolutionMeta parent) {
        this.parent = parent;
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
    public boolean equals(Object obj) {
        if(obj instanceof SolutionMetaStep) {
            SolutionMetaStep sms = (SolutionMetaStep)obj;
            return sms.getHint().equals(getHint()) && sms.getText().equals(getText());
        }
        else {
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return "SolutionMetaStep [hint=" + hint + ", text=" + text + ", figure=" + figure + "]";
    }
}

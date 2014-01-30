package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

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
    public String toString() {
        return "SolutionMetaStep [hint=" + hint + ", text=" + text + ", figure=" + figure + "]";
    }
}

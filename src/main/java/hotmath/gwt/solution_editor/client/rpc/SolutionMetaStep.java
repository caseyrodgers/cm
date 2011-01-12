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
    
    public SolutionMetaStep() {
    }
    
    public SolutionMetaStep(String hint, String text) {
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
}

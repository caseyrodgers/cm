package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.SpellCheckResults;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class SpellCheckAction implements Action<SpellCheckResults>{
    
    private String text;

    public SpellCheckAction() {}

    public SpellCheckAction(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}

package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class SolutionExtractResults implements Response {
    
    private String text;

    public SolutionExtractResults() {}
    
    public SolutionExtractResults(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    

}

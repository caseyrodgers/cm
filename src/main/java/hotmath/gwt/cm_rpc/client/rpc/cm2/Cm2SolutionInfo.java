package hotmath.gwt.cm_rpc.client.rpc.cm2;

import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.cm_rpc.client.rpc.SolutionWidgetResult;

public class Cm2SolutionInfo  {
    private String probStatement;
    private SolutionResponse solutionResponse;
    private SolutionWidgetResult widgetResult;

    public Cm2SolutionInfo(){}
    
    public Cm2SolutionInfo(String probStatement, SolutionResponse solutionResponse) {
        this.probStatement = probStatement;
        this.solutionResponse = solutionResponse;
    }

    public void setWidgetResult(SolutionWidgetResult widgetResult) {
        this.widgetResult = widgetResult;
    }
    
    public SolutionWidgetResult getWidgetResult() {
        return widgetResult;
    }
    

    public SolutionResponse getSolutionResponse() {
        return solutionResponse;
    }

    public void setSolutionResponse(SolutionResponse solutionResponse) {
        this.solutionResponse = solutionResponse;
    }

    public String getProbStatement() {
        return probStatement;
    }

    public void setProbStatement(String probStatement) {
        this.probStatement = probStatement;
    }
    
}

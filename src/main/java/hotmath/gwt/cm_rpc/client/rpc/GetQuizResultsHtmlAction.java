package hotmath.gwt.cm_rpc.client.rpc;



public class GetQuizResultsHtmlAction implements Action<QuizResultsMetaInfo>{
    
    int runId;

    public GetQuizResultsHtmlAction(){}
    
    public GetQuizResultsHtmlAction(int runId) {
        this.runId = runId; 
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }


    @Override
    public String toString() {
        return "GetQuizResultsHtmlAction [runId=" + runId +  "]";
    }
}

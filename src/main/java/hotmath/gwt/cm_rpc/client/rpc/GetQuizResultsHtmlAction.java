package hotmath.gwt.cm_rpc.client.rpc;



public class GetQuizResultsHtmlAction implements Action<QuizResultsMetaInfo>{
    
    int runId;
    boolean allowPdfIfExist;
    
    public GetQuizResultsHtmlAction(){}
    
    public GetQuizResultsHtmlAction(int runId, boolean allowPdfIfExist) {
        this.runId = runId; 
        this.allowPdfIfExist = allowPdfIfExist;
    }
    
    public GetQuizResultsHtmlAction(int runId) {
        this(runId, true);
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public boolean isAllowPdfIfExist() {
        return allowPdfIfExist;
    }

    public void setAllowPdfIfExist(boolean allowPdfIfExist) {
        this.allowPdfIfExist = allowPdfIfExist;
    }

    @Override
    public String toString() {
        return "GetQuizResultsHtmlAction [runId=" + runId + ", allowPdfIfExist=" + allowPdfIfExist + "]";
    }
}

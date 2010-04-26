package hotmath.gwt.cm_rpc.client.rpc;



public class GetQuizHtmlSpritedAction implements Action<QuizHtmlResult> {
    
    int uid;
    int testId;
    int testSegment;
    boolean loadActive;
    
    public boolean isLoadActive() {
        return loadActive;
    }

    public void setLoadActive(boolean loadActive) {
        this.loadActive = loadActive;
    }

    public GetQuizHtmlSpritedAction() {
        super();
    }
    
    public GetQuizHtmlSpritedAction(Integer uid, Integer testId, Integer testSegment) {
        this.uid = uid;
        this.testId = testId;
        this.testSegment = testSegment;
    }

    public int getTestSegment() {
        return testSegment;
    }

    public void setTestSegment(int testSegment) {
        this.testSegment = testSegment;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }
}

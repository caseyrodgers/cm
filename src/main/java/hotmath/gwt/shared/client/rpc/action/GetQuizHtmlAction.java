package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.QuizHtmlResult;

public class GetQuizHtmlAction implements Action<QuizHtmlResult> {

    int uid;
    int testSegment;
    int testId;

    public GetQuizHtmlAction() {}
    
    /** If testId not set, then active settings are used
     * 
     * @param uid
     * @param testId
     * @param testSegment
     */
    public GetQuizHtmlAction(int uid, int testSegment) {
        this.uid = uid;
        this.testSegment = testSegment;
    }
    
    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getTestSegment() {
        return testSegment;
    }

    public void setTestSegment(int testSegment) {
        this.testSegment = testSegment;
    }

    
    @Override
    public String toString() {
        return "GetQuizHtmlAction [testId=" + testId + ", testSegment=" + testSegment + ", uid=" + uid + "]";
    }
}

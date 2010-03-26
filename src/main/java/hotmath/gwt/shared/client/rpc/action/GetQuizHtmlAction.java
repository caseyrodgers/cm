package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.QuizHtmlResult;

/** Defines the movement of the active test by specifying
 * the segment of the current program.
 * 
 * Maybe a better name is GetUserProgramSegmentAction(uid, testSegment);
 * @author casey
 *
 */
public class GetQuizHtmlAction implements Action<QuizHtmlResult> {

    int uid;
    int testSegment;
    int testId;
    boolean loadActive;

    public GetQuizHtmlAction() {}
    
    /** If testId not set, then active settings are used
     * 
     * @param uid
     * @param testId
     * @param testSegment
     */
    public GetQuizHtmlAction(int uid, int testId, int testSegment) {
        this.uid = uid;
        this.testId = testId;
        this.testSegment = testSegment;
    }

    public boolean isLoadActive() {
        return loadActive;
    }

    public void setLoadActive(boolean loadActive) {
        this.loadActive = loadActive;
    }

    public int getUid() {
        return uid;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
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
        return "GetQuizHtmlAction [loadActive=" + loadActive + ", testId=" + testId + ", testSegment=" + testSegment
                + ", uid=" + uid + "]";
    }
}

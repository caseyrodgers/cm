package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.QuizHtmlResult;

/** Defines the movement of the active test by specifing
 * the segment of the current program.
 * 
 * Maybe a better name is GetUserProgramSegmentAction(uid, testSegment);
 * @author casey
 *
 */
public class GetQuizHtmlAction implements Action<QuizHtmlResult> {

    int uid;
    int testSegment;
    boolean loadActive;

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
    

    public boolean isLoadActive() {
        return loadActive;
    }

    public void setLoadActive(boolean loadActive) {
        this.loadActive = loadActive;
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
        return "GetQuizHtmlAction [testSegment=" + testSegment + ", uid=" + uid + "]";
    }
}

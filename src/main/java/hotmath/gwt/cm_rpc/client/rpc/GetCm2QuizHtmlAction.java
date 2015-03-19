package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;


/** Defines the movement of the active test by specifying
 * the segment of the current program.
 * 
 * Maybe a better name is GetUserProgramSegmentAction(uid, testSegment);
 * @author casey
 *
 */
public class GetCm2QuizHtmlAction implements Action<QuizCm2HtmlResult> {

    int testId;

    public GetCm2QuizHtmlAction() {}
    
    public GetCm2QuizHtmlAction(int testId) {
        this.testId = testId;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }
}

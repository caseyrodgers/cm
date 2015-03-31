package hotmath.gwt.cm_rpc.client.rpc.cm2;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;

/** Check the named quiz and return the prescription data
 * 
 * @author casey
 *
 */
public class CheckCm2QuizAction implements Action<QuizCm2CheckedResult> {

    int testId;

    public CheckCm2QuizAction() {}
    
    public CheckCm2QuizAction(int testId) {
        this.testId = testId;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }
}

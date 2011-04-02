package hotmath.gwt.cm_rpc.client.rpc;



/** GetQuizHTML RPC server response
 * 
 * @author casey
 *
 */
public class QuizHtmlResponse implements Response {
    
    CmProgramFlowAction nextAction;
    QuizHtmlResult htmlResult;

    public QuizHtmlResponse() {}

    public QuizHtmlResponse(QuizHtmlResult htmlResult, CmProgramFlowAction nextAction) {
        this.htmlResult = htmlResult;
        this.nextAction = nextAction;
    }

    public CmProgramFlowAction getNextAction() {
        return nextAction;
    }

    public void setNextAction(CmProgramFlowAction nextAction) {
        this.nextAction = nextAction;
    }

    public QuizHtmlResult getHtmlResult() {
        return htmlResult;
    }

    public void setHtmlResult(QuizHtmlResult htmlResult) {
        this.htmlResult = htmlResult;
    }
}

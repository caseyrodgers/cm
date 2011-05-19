package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.ui.CmProgramFlowClientManager.Callback;
import hotmath.gwt.cm.client.ui.context.QuizCheckResultsWindow;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_tools.client.ui.CmLogger;

public class StandardFlowCallback implements Callback {
    
    @Override
    public void programFlow(CmProgramFlowAction flowResponse) {
        switch(flowResponse.getPlace()) {
            case QUIZ:
                CatchupMath.getThisInstance().showQuizPanel(flowResponse.getQuizResult());
                break;
                
            case PRESCRIPTION:
                CatchupMath.getThisInstance().showPrescriptionPanel(flowResponse.getPrescriptionResponse());
                break;
                
            case AUTO_ADVANCED_PROGRAM:
                QuizCheckResultsWindow.autoAdvanceUser();
                break;
                
            case END_OF_PROGRAM:
                CatchupMath.getThisInstance().showEndOfProgramPanel();
                break;
                
            case WELCOME:
                CatchupMath.getThisInstance().showWelcomePanel();
                break;
                
            default:
                CmLogger.error("Invalid program flow state: " + flowResponse);
                break;
        }
    }
}

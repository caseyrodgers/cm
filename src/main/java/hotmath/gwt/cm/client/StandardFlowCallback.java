package hotmath.gwt.cm.client;

import com.allen_sauer.gwt.log.client.Log;

import hotmath.gwt.cm.client.ui.context.QuizCheckResultsWindow;
import hotmath.gwt.cm_core.client.flow.CmProgramFlowClientManager.CmProgramFlowClientManagerCallback;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;

public class StandardFlowCallback implements CmProgramFlowClientManagerCallback {
    
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


    @Override
    public void flowError(Throwable caught) {
        Log.error("Flow Error",  caught);
        CmMessageBox.showAlert("Flow Error",  caught.getMessage());
    }
    
    @Override
    public int getUid() {
        return UserInfo.getInstance().getUid();
    }
}

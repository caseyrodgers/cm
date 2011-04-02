package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.ui.context.QuizCheckResultsWindow;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

/** Abstraction around a CM program
 * 
 *  Controls a user's progression through 
 *  a CM program.
 * 
 * @author casey
 *
 */
public class CmProgramFlowClientManager {
    
    /** request the program flow be advanced
     *  
     * @param callback
     */
    static public void getNextActiveProgramState(final Callback callback) {
        getProgramState(callback, FlowType.NEXT);
    }
    
    static public void getActiveProgramState(final Callback callback) {
        getProgramState(callback, FlowType.ACTIVE);
    }
    static private void getProgramState(final Callback callback, final FlowType flowType) {

        new RetryAction<CmProgramFlowAction>() {
            
            @Override
            public void attempt() {
                CatchupMathTools.setBusy(true);
                GetCmProgramFlowAction action = new GetCmProgramFlowAction(UserInfo.getInstance().getUid(), flowType);
                setAction(action);
                CmLogger.info("showActiveProgramState: " + action);
                CmShared.getCmService().execute(action, this);
            }
            
            @Override
            public void oncapture(CmProgramFlowAction nextAction) {
                CatchupMathTools.setBusy(false);
                
                callback.programFlow(nextAction);
            }
        }.register();       
    }    
    
    static public void moveToNextInProgramFlow() {
        CmProgramFlowClientManager.getNextActiveProgramState(new Callback() {
            
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
        });
        
    }

    public static interface Callback {
        void programFlow(CmProgramFlowAction flowResponse);
    }
}

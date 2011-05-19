package hotmath.gwt.cm.client.ui;

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
    
    static public void retakeActiveProgramSegment(final Callback callback) {
        getProgramState(callback, FlowType.RETAKE_SEGMENT);
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

    
    static Callback __callBack;
    static {
        __callBack = new StandardFlowCallback();
    }

    static public void moveToNextSegmentInProgram() {
        CmProgramFlowClientManager.getNextActiveProgramState(__callBack);
    }
    
    
    static public void retakeProgramSegment() {
        CmProgramFlowClientManager.retakeActiveProgramSegment(__callBack);
    }

    public static interface Callback {
        void programFlow(CmProgramFlowAction flowResponse);
    }
}

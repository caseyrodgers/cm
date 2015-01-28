package hotmath.gwt.cm_core.client.flow;

import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Abstraction around a CM program
 * 
 *  Controls a user's progression through 
 *  a CM program.
 *  
 *  Provides a high-level API.
 * 
 * @author casey
 *
 */
public class CmProgramFlowClientManager {
    
    /** request the program flow be advanced
     *  
     * @param callback
     */
    static public void getNextActiveProgramState(final CmProgramFlowClientManagerCallback callback) {
        getProgramState(callback, FlowType.NEXT);
    }
    
    static public void getActiveProgramState(final CmProgramFlowClientManagerCallback callback) {
        getProgramState(callback, FlowType.ACTIVE);
    }
    
    static public void retakeActiveProgramSegment(final CmProgramFlowClientManagerCallback callback) {
        getProgramState(callback, FlowType.RETAKE_SEGMENT);
    }
    
    static private void getProgramState(final CmProgramFlowClientManagerCallback callback, final FlowType flowType) {

            CmBusyManager.setBusy(true);
            GetCmProgramFlowAction action = new GetCmProgramFlowAction(callback.getUid(), flowType);
            CmRpcCore.getCmService().execute(action, new AsyncCallback<CmProgramFlowAction>() {
                @Override
                public void onSuccess(CmProgramFlowAction result) {
                    CmBusyManager.setBusy(false);
                    callback.programFlow(result);
                }
                @Override
                public void onFailure(Throwable caught) {
                    CmBusyManager.setBusy(false);
                    callback.flowError(caught);
                }
            });
    }    

    public static CmProgramFlowClientManagerCallback __callBack;
    static public void setFlowCallback(CmProgramFlowClientManagerCallback callback) {
        __callBack = callback;
    }

    static public void moveToNextSegmentInProgram() {
        CmProgramFlowClientManager.getNextActiveProgramState(getCallback());
    }
    
    
    private static CmProgramFlowClientManagerCallback getCallback() {
        if(__callBack == null) {
            Window.alert("No CM Flow callback installed");
        }
        return __callBack;
    }

    static public void retakeProgramSegment() {
        CmProgramFlowClientManager.retakeActiveProgramSegment(getCallback());
    }

    public static interface CmProgramFlowClientManagerCallback {
        void programFlow(CmProgramFlowAction flowResponse);

        int getUid();

        void flowError(Throwable caught);
    }
}

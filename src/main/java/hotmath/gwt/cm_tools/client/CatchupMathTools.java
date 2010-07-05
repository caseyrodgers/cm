package hotmath.gwt.cm_tools.client;

import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CatchupMathVersionInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.action.GetCmVersionInfoAction;
import hotmath.gwt.shared.client.rpc.result.CmVersionInfo;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CatchupMathTools implements EntryPoint {
    
    public void onModuleLoad() {
    	CmLogger.info("Catchup Math Tools library loaded successfully");
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoadLocal() {
        CmLogger.info("Catchup Math Tools library loaded successfully");
    }

    static public void setBusy(boolean trueFalse) {
    	CmBusyManager.setBusy(trueFalse);
    }
    
    
    /** Force reset of the isBusy stack allowing 
     *  for an exception to happen and display
     *  an alert message.
     */
    static public void resetBusy() {
    	CmBusyManager.resetBusy();
    }

    
    /**
     * Display standard message dialog
     * 
     * @param msg
     */
    static public void showAlert(String msg) {
        showAlert("Info", msg);
    }

    static public void showAlert(String title, String msg) {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        MessageBox.alert(title, msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
            }
        });
    }

    static public void showAlert(String title, String msg, final CmAsyncRequest callback) {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        MessageBox.alert(title, msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                if (callback != null)
                    callback.requestComplete(be.getValue());
                
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
            }
        });
    }

    static public void showAlert(String msg, final CmAsyncRequest callback) {
        showAlert("Info", msg, callback);
    }

    
    static public String FEEDBACK_MESSAGE="<p>Please use the Help button to send us feedback.</p>";
    
    
    /** Call server to get version information, send the URL
     *  that serves this app.
     *  
     */
    static public void showVersionInfo() {

        CmServiceAsync s = (CmServiceAsync) CmShared.getCmService();
        GetCmVersionInfoAction action = new GetCmVersionInfoAction(CmShared.CM_HOME_URL);
        s.execute(action, new AsyncCallback<CmVersionInfo>() {

            public void onSuccess(CmVersionInfo version) {
                CatchupMathTools.showAlert(version.toString());
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                msg += " Build version: " + CatchupMathVersionInfo.getBuildVersion();
                CatchupMathTools.showAlert(msg);
            }
        });
    }    
}

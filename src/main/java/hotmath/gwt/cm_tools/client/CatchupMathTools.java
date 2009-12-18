package hotmath.gwt.cm_tools.client;

import hotmath.gwt.cm_tools.client.service.CmService;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.GetCmVersionInfoAction;
import hotmath.gwt.shared.client.rpc.result.CmVersionInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class CatchupMathTools implements EntryPoint {
    
    public void onModuleLoad() {
        Log.setUncaughtExceptionHandler();

        DeferredCommand.addCommand(new Command() {
          public void execute() {
              onModuleLoadLocal();
          }
        });
      }
    
    static {
        setupServices();
    }
    /**
     * This is the entry point method.
     */
    public void onModuleLoadLocal() {
        Log.info("Catchup Math Tools library loaded successfully");
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
        EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_MODAL_WINDOW_OPEN));
        MessageBox.alert(title, msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
            }
        });
    }

    static public void showAlert(String title, String msg, final CmAsyncRequest callback) {
        MessageBox.alert(title, msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                if (callback != null)
                    callback.requestComplete(be.getValue());
            }
        });
    }

    static public void showAlert(String msg, final CmAsyncRequest callback) {
        showAlert("Info", msg, callback);
    }

    /**
     * Register any RPC services with the system
     * 
     */
    static private void setupServices() {
        Log.info("CatchupMathTools: Setting up services");
        
        
        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";
        
        final CmServiceAsync cmService = (CmServiceAsync)GWT.create(CmService.class);
        
        
        ((ServiceDefTarget) cmService).setServiceEntryPoint(point + "services/cmService");
        Registry.register("cmService", cmService);        
    }
    
    static public String FEEDBACK_MESSAGE="<p>Please use the Help button to send us feedback.</p>";
    
    
    
    

    /** Call server to get version information, send the URL
     *  that serves this app.
     *  
     */
    static public void showVersionInfo() {

        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        GetCmVersionInfoAction action = new GetCmVersionInfoAction(CmShared.CM_HOME_URL);
        s.execute(action, new AsyncCallback<CmVersionInfo>() {

            public void onSuccess(CmVersionInfo version) {
                CatchupMathTools.showAlert(version.toString());
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathTools.showAlert(msg);
            }
        });
    }    
}

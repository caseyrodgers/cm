package hotmath.gwt.cm_tools.client;

import hotmath.gwt.cm_tools.client.service.CmService;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.service.PrescriptionService;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.shared.client.data.CmAsyncRequest;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

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

    /**
     * Display or hide the modal busy dialog
     * 
     * @param trueFalse
     */
    static public void setBusy(boolean trueFalse) {
        RootPanel.get("loading").setVisible(trueFalse);
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
        setBusy(false);
        MessageBox.alert(title, msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
            }
        });
    }

    static public void showAlert(String title, String msg, final CmAsyncRequest callback) {
        setBusy(false);
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
        
        final PrescriptionServiceAsync prescriptionService = (PrescriptionServiceAsync) GWT.create(PrescriptionService.class);

        ((ServiceDefTarget) prescriptionService).setServiceEntryPoint(point + "services/prescriptionService");
        Registry.register("prescriptionService", prescriptionService);
        
        
        final CmServiceAsync cmService = (CmServiceAsync)GWT.create(CmService.class);
        
        
        ((ServiceDefTarget) cmService).setServiceEntryPoint(point + "services/cmService");
        Registry.register("cmService", cmService);        
    }
    
    static public String FEEDBACK_MESSAGE="<p>Please send us feedback, by using Feedback at the bottom of the page.</p>";    
}

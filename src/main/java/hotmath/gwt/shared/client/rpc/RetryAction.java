package hotmath.gwt.shared.client.rpc;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.util.NotActiveProgramWindow;
import hotmath.gwt.shared.client.util.SystemVersionUpdateChecker;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.StatusCodeException;

/** Provide retry operation for GWT RPC 
 * 
 *  implement RPC in attempt method, and callback in the oncapture method.
 *  
 *  If an error occurred, the user is given the opportunity to
 *  retry the operation (resulting in another call to attempt).
 *  
 *   Example usage:
 *  <pre>
    new RetryAction<MyData> {
        public void attempt() {
            cmServer.execute(action);
        }
        public void oncapture(MyData data) {
           processMyData(data) 
       }
    }.register();
 </pre>
 *
 * 
 * 
 * Caller should set the action by calling setAction to allow for automatic
 * server reporting of failed action.
 * 
 * 
 *  NOTE: We are using Window.confirm instead of a GXT window to handle 
 *  zorder issues and make sure the process is Synchronous and stops
 *  Queuing errors.
 *  
 *  Provides automatic retry of operations.  After auto retry the user
 *  is presented with a server error dialog in which they can choose to 
 *  either continue or cancel.
 
 * @author casey
 *
 * @param <T>
 */
public abstract class RetryAction<T> implements AsyncCallback<T> {
    
    
    /** keep a count of RetryActions created
     * 
     */
    static int __counter;
    
    public abstract void attempt();
    public abstract void oncapture(T value);

    int MAX_RETRY=3;
    int _retryNumber=1;
    long _timeStart;
    int instanceCount;
    
    public RetryAction() {
        instanceCount = __counter++;
        CmLogger.info("RetryAction " + instanceCount + " created: " + getClass().getName());
    }
    
    public void setStartTime() {
        _timeStart = System.currentTimeMillis();
    }

    /** add this action to the RetryActionManager 
     * for queued execution 
     */
    public void register() {
        RetryActionManager.getInstance().registerAction(this);
    }

    public void onSuccess(T value) {
        CmLogger.info("RetryAction " + instanceCount + " success [" + getRequestTime() + "] (" + activeAction + ") : " 
                + getClass().getName());
        
        RetryActionManager.getInstance().requestComplete(this);
        
        try {
            oncapture(value);
        } catch (RuntimeException error) {
            onFailure(error);
        }
    }

    public void onFailure(Throwable error) {
        RetryActionManager.getInstance().requestComplete(this);
        
        
//        /** provide auto retry of operation
//         * 
//         */
//        if(_retryNumber<MAX_RETRY) {
//            _retryNumber++;
//            CmLogger.info("RetryAction " + instanceCount + " retry #" + _retryNumber + " [" + getRequestTime() + "] (" + activeAction + "): " + getClass().getName());
//            attempt();
//            return;
//        }
        
        error.printStackTrace();
        CmLogger.info("RetryAction " + instanceCount + " failure [" + getRequestTime() + "] (" + activeAction + ") : " + getClass().getName());
        
        CmBusyManager.resetBusy();
        
        try {
            throw error;
        } catch (IncompatibleRemoteServiceException remoteServiceException) {
            onNewVersion();
        } catch (SerializationException serializationException) {
            onNewVersion();
        } catch (Throwable throwable) {
            String message = throwable.getLocalizedMessage();

            if (message == null) {
                message = throwable.getMessage();
            }

            if (message == null) {
                message = throwable.getClass().getName();
            }

            /** 
             *   Catchup Math login problems cannot be retried 
             *   @TODO: maybe a 'ActionRetryable' or 'RetryableAction' interface?
             */
            if(message.indexOf("CmExceptionLogin") > -1 
                    || message.indexOf("Security") > -1
                    || message.indexOf("login key") > -1) {
                CatchupMathTools.showAlert("Login Problem", "You could not be logged in.  Please try again", new CmAsyncRequestImplDefault() {
                    @Override
                    public void requestComplete(String requestData) {
                        Window.Location.assign(CmShared.CM_HOME_URL);
                    }
                });
            }
            else if(message.indexOf("UserProgramIsNotActiveException") > -1) {
                /** if current program is Auto-Enroll, then allow forced changes
                 *  to programs without user message
                 */
                String tn = UserInfo.getInstance().getTestName();
                System.out.println(tn);
                
                /** Exception is thrown if Program has been changed */
                new NotActiveProgramWindow();                
            }
            else {
                
                if( isInformationalOnlyException(error)){
                    InfoPopupBox.display("Information",error.getMessage());
                }
                else {
                    sendInfoAboutRetriedCommand("failed", throwable);
                    
                    /** Perform as synchronous call in Window.confirm to stop
                     *  the flow of new requests until this one is taken care of.
                     */
                    String msg = "A server error has occurred.\n" 
                    + "You may retry this operation by clicking 'OK'.\n"
                    + "However if the error persists, contact Technical Support.";
                    
                    if(CmShared.getQueryParameter("debug") != null) {
                        msg += "\n\n" + throwable.getMessage();
                    }
                    
                    if(Window.confirm(msg)) {
                        sendInfoAboutRetriedCommand("retried", null);
                        attempt();
                    }
                    else {
                        sendInfoAboutRetriedCommand("canceled",null);
                    	onCancel();
                    }
                }
            }
        }
    }

    private boolean isInformationalOnlyException(Throwable th) {
        if(th.getMessage().indexOf("test has already been checked") > -1) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /*
     * possible usage: close a window that is not functional when an action fails
     */
    public void onCancel() {
        
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        
        CmLogger.info("RetryAction " + instanceCount + " cancel [" + getRequestTime() + "] (" + activeAction + ") : " 
                + getClass().getName());

        
        final CmWindow win = new CmWindow();
        win.setHeading("Server Warning");
        win.setSize(350,200);
        win.setModal(true);
        win.setResizable(false);
        
        String msg = "<p style='padding: 15px;font-size: 105%'>" +
                    "Catchup Math has encountered an exception. " +
                    "Please press the F5 key on your keyboard to continue. " +
                    "Sorry for the inconvenience!" +
                    "</p>";
        
        win.setLayout(new FitLayout());
        win.add(new Html(msg));
        
        win.addButton(new Button("Continue",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                win.close();
            }
        }));
        
        win.setVisible(true);
    }
    
    public void onNewVersion() {
        new SystemVersionUpdateChecker();        
    }
    
    public Action<? extends Response> activeAction;
    
    public Action<? extends Response> getAction() {
        return activeAction;
    }
    
    /** Set the action that is reported when a retry operation fails
     * 
     * @param action
     */
    public void setAction(Action<? extends Response> action) {
        this.activeAction = action;
    }    

    private long getRequestTime() {
        return System.currentTimeMillis() - _timeStart;
    }
    
    /** send a server request to track the fact
     *  this command failed.  Make sure this command
     *  does not start of chain-reaction of failures by
     *  not executing in RetryAction and fail silently.
     *  
     */
    private void sendInfoAboutRetriedCommand(String logType, Throwable throwable) {

        try {
            String nameAndTime = getClass().getName() + ": " + getRequestTime() + " mills, counter; " + instanceCount ;
            CmShared.getCmService().execute(
                    new LogRetryActionFailedAction(logType, UserInfo.getInstance().getUid(),nameAndTime,getAction(),CmShared.getStackTraceAsString(throwable)),
                    new AsyncCallback<RpcData>() {
                @Override
                public void onSuccess(RpcData result) {
                    CmLogger.info("Retry operation logged");
                }

                @Override
                public void onFailure(Throwable exe) {
                    if(CmShared.getQueryParameter("debug") != null)
                        Window.alert("Error sending info about retry action: " + exe);
                }
            });
        }
        catch(Exception e) {
            CmLogger.error("RetryAction " + instanceCount + " Error calling LogRetryActinoFailedAction", e);
        }
    }
    

}
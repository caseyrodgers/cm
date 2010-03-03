package hotmath.gwt.shared.client.rpc;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import com.allen_sauer.gwt.log.client.Log;
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
    }.attempt();
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
 *  queueing errors.
 
 * @author casey
 *
 * @param <T>
 */
public abstract class RetryAction<T> implements AsyncCallback<T> {
    public abstract void attempt();
    public abstract void oncapture(T value);
    
    long _timeStart;
    public RetryAction() {
        _timeStart = System.currentTimeMillis();
        Log.info("RetryRequest created: " + getClass().getName());
    }


    public void onSuccess(T value) {
        Log.info("RetryAction succesful: " + getClass().getName() + ", " + getRequestTime());
        try {
            oncapture(value);
        } catch (RuntimeException error) {
            onFailure(error);
        }
    }

    public void onFailure(Throwable error) {
        error.printStackTrace();
        Log.info("RetryAction failed", error);
        
        CmBusyManager.resetBusy();
        
        try {
            throw error;
        } catch (IncompatibleRemoteServiceException remoteServiceException) {
            Window.alert("A fatal error occurred, you should refresh this page and try again");
        } catch (SerializationException serializationException) {
            Window.alert("A fatal error occurred, you should refresh this page and try again");
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
            else {
                
                sendInfoAboutRetriedCommand("failed", throwable);
                
                /** Perform as synchronous call in Window.confirm to stop
                 *  the flow of new requests until this one is taken care of.
                 */
                String msg = "A server error has occured.\n" 
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

    
    /*
     * possible usage: close a window that is not functional when an action fails
     */
    public void onCancel() {
        Log.info("RetryAction canceled: " + getRequestTime());
        
        final CmWindow win = new CmWindow();
        win.setHeading("Server Warning");
        win.setSize(350,200);
        win.setModal(true);
        win.setResizable(false);
        
        String msg = "<p style='padding: 15px;font-size: 105%'>There has been a server error.  " +
                    "You can continue, but be aware of possible side effects.  " +
                    "If you continue to have errors we suggest you refresh this page " +
                    "by pressing the F5 key.</p>";
        
        win.setLayout(new FitLayout());
        win.add(new Html(msg));
        
        win.addButton(new Button("Continue",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                win.close();
            }
        }));
        
        win.setVisible(true);
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
            String nameAndTime = getClass().getName() + ": " + getRequestTime() + " mills";
            CmShared.getCmService().execute(
                    new LogRetryActionFailedAction(logType, UserInfo.getInstance().getUid(),nameAndTime,getAction(),getStackTraceAsString(throwable)),
                    new AsyncCallback<RpcData>() {
                @Override
                public void onSuccess(RpcData result) {
                    Log.info("Retry operation logged");
                }

                @Override
                public void onFailure(Throwable exe) {
                    if(CmShared.getQueryParameter("debug") != null)
                        Window.alert("Error sending info about retry action: " + exe);
                }
            });
        }
        catch(Exception e) {
            Log.error("Error calling LogRetryActinoFailedAction", e);
        }
    }
    
    /** Return stack trace as string, or null if 
     * throwable is null.
     * 
     * @param th
     * @return
     */
    private String getStackTraceAsString(Throwable th) {
        if(th == null)
            return null;
        
        
        final StringBuilder result = new StringBuilder();
        if(th instanceof StatusCodeException)
            result.append("HTTP ERROR CODE: ").append(((StatusCodeException)th).getStatusCode()).append("\n");
                
        result.append(th.toString()).append("\n");

        for (StackTraceElement element : th.getStackTrace() ){
            result.append( element ).append("\n");
        }
        return result.toString();
    }
}
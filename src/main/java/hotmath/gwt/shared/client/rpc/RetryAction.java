package hotmath.gwt.shared.client.rpc;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;

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

    public void onFailure(Throwable error) {
        
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
             *   atchup Math login problems cannot be retried 
             *   @TODO: maybe a 'ActionRetryable' interface?
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
                String msg = "An error occured:\n" + message + "\n"
                + "You may retry this operation by clicking 'OK'.\n"
                + "However if the error persists, contact Technical Support.";
                
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
                CatchupMathTools.showAlert("Retryable Operation", msg, new CmAsyncRequestImplDefault() {
                    @Override
                    public void requestComplete(String requestData) {
                        attempt();                        
                    }
                });                
            }
        }
    }

    public void onSuccess(T value) {
        try {
            oncapture(value);
        } catch (RuntimeException error) {
            onFailure(error);
        }
    }
}
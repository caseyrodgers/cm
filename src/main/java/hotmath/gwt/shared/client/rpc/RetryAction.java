package hotmath.gwt.shared.client.rpc;

import hotmath.gwt.cm_tools.client.CmBusyManager;

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

            String msg = "An error occured:\n" + message + "\n"
            + "You may retry this operation by clicking 'OK'.\n"
            + "However if the error persists, contact Technical Support.";
            
            if(Window.confirm(msg)) {
                attempt();
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
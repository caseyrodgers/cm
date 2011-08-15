package hotmath.gwt.shared.client.util;


import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.shared.client.CmShared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Display a message from central server
 * 
 * @author casey
 *
 */
public class CmPollingLoop  {

    static final int CHECK_EVERY = 1000 * 60; //  * 15;

    List<ActionEntry> actions = new ArrayList<ActionEntry>();
    
    private CmPollingLoop() {
    }
    

    /** Monitor server for version changes.
     * 
     * Checks every N minutes.
     * 
     */
    public void monitorVersionChanges() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                checkForUpdate();
            }
        };
        timer.scheduleRepeating(CHECK_EVERY);
    }
    
    
    /** Call server and look for new messages.
     * 
     * Only call server if window is not currently being displayed.
     */
    public void checkForUpdate() {

    	if(actions.size() == 0) {
    		return;
    	}
    	
    	MultiActionRequestAction mutiAction = new MultiActionRequestAction();
    	for(ActionEntry ae: actions) {
    		mutiAction.getActions().add(ae.action);
    	}
        CmShared.getCmService().execute(mutiAction, new AsyncCallback<CmList<Response>>() {
             @Override
            public void onSuccess(CmList<Response> info) {
            	 /** should be a one-to-one in actions/responses */
            	 assert(info.size() != actions.size());
            	 for(int i=0,t=info.size();i<t;i++) {
            		 //actions.get(i).callback.onSuccess(info.get(i));
            	 }
            }
             @Override
            public void onFailure(Throwable arg0) {
                 /** fail silent */
                 // CatchupMathTools.showAlert(arg0.getMessage());
            }
        });
    }
    
    static public class ActionEntry {
    	Action<?> action;
    	AsyncCallback<? > callback;
    }
}

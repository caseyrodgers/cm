package hotmath.gwt.shared.client.rpc;

import hotmath.gwt.cm_tools.client.ui.CmLogger;

import java.util.ArrayList;
import java.util.List;

/** Manages a queue of action requests making
 *  sure only one request executes at a time.
 *  
 * @author casey
 *
 */
public class RetryActionManager {
    
    static private RetryActionManager __instance;
    static public RetryActionManager getInstance() {
        if(__instance == null) {
            __instance = new RetryActionManager();
        }
        return __instance;
    }
    
    
    /** track if the 'single' request thread is busy
     * 
     */
    boolean _busy=false;
    
    @SuppressWarnings("unchecked")
    List<RetryAction> _actions = new ArrayList<RetryAction>();
    private RetryActionManager() {
        /** start up action queue watcher */
        new RetryActionManagerQueueWatcher();
    }
    
    @SuppressWarnings("unchecked")
    public void registerAction(RetryAction action) {
        _actions.add(action);

        CmLogger.debug("RetryActionManager: registerAction (" + _actions.size() + "): " + action);
        
        checkQueue();
    }
    
    @SuppressWarnings("unchecked")
    public void requestComplete(RetryAction action) {
        CmLogger.debug("RetryActionManager: requestComplete: " + action);
        _busy = false;
        checkQueue();
    }
    
    
    public List<RetryAction> getQueue() {
        return _actions;
    }
    
    /** look in the queue for new actions to execute
     * 
     * If the request thread is currently executing, then exit.
     * The RetryAction will call back to this method once it is completed
     * from the current request, which will trigger the next action to fire.
     * 
     * 
     */
    @SuppressWarnings("unchecked")    
    public void checkQueue() {
        if(_busy) {
            CmLogger.debug("RetryActionManager: checkQueue (" + _actions.size() + "): isBusy");
            return;
        }
        
        if(_actions.size() > 0) {
            int s=_actions.size()-1;
            RetryAction action = _actions.get(s);
            _actions.remove(s);
            
            CmLogger.debug("RetryActionManager: checkQueue attempt: " + action);            
            _busy = true;
            action.attempt();
        }
    }
    
    /** Return information about the Retry queue
     * 
     */
    public String toString() {
        String msg = "Request Queue: " + _actions;
        return msg;
    }
}

package hotmath.gwt.shared.client.rpc;

import hotmath.gwt.cm_tools.client.ui.CmLogger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/** Manages a queue of action requests making
 *  sure only one request executes at a time.
 *  
 * @author casey
 *
 */
public class RetryActionManager {

	static final int QUEUE_SIZE = 5;

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
    
    @SuppressWarnings("rawtypes")
    List<RetryAction> _actions = new ArrayList<RetryAction>();

    @SuppressWarnings("rawtypes")
    Queue<RetryAction> _queue = new LinkedList<RetryAction>();

    private RetryActionManager() {
        /** start up action queue watcher
        new RetryActionManagerQueueWatcher();
        */
    }
    
    @SuppressWarnings("rawtypes")
    public void registerAction(RetryAction action) {
        _actions.add(action);

        CmLogger.debug("RetryActionManager: registerAction (" + _actions.size() + "): " + action);
        
        checkQueue();
    }
    
    @SuppressWarnings("rawtypes")
    public void requestComplete(RetryAction action) {
        CmLogger.debug("RetryActionManager: requestComplete: " + action);
        _busy = false;

        /*
         * only retain QUEUE_SIZE most recently completed actions
         */
        _queue.add(action);
        if (_queue.size() > QUEUE_SIZE) _queue.remove();
        
        checkQueue();
    }
    

    @SuppressWarnings("rawtypes")
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
    @SuppressWarnings("rawtypes")    
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
            
            action.setStartTime();
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

    @SuppressWarnings("rawtypes")
    public Queue<RetryAction> getCompletedActions() {
    	return _queue;
    }
}

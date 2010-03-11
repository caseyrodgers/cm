package hotmath.gwt.shared.client.rpc;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;

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
    }
    
    @SuppressWarnings("unchecked")
    public void registerAction(RetryAction action) {
        _actions.add(action);

        Log.debug("RetryActionManager: registerAction (" + _actions.size() + "): " + action);
        
        checkQueue();
    }
    
    
    @SuppressWarnings("unchecked")
    public void requestComplete(RetryAction action) {
        Log.debug("RetryActionManager: requestComplete: " + action);
        _busy = false;
        checkQueue();
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
            Log.debug("RetryActionManager: checkQueue (" + _actions.size() + "): isBusy");
            return;
        }
        
        if(_actions.size() > 0) {
            int s=_actions.size()-1;
            RetryAction action = _actions.get(s);
            _actions.remove(s);
            
            Log.debug("RetryActionManager: checkQueue attempt: " + action);            
            _busy = true;
            action.attempt();
        }
    }
}

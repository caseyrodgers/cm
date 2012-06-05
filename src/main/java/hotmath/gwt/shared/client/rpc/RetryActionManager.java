package hotmath.gwt.shared.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.GetQuizResultsHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveFeedbackAction;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CmShared;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gwt.user.client.rpc.AsyncCallback;

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
    public void addActionToReportQueue(RetryAction retryAction) {
        /*
         * only retain QUEUE_SIZE most recent retryActions
         */
        _queue.add(retryAction);
        if (_queue.size() > QUEUE_SIZE) _queue.remove();
    }

    private String createActionQueueStack() {
        String msg = "";
        
        for(RetryAction<?> ra: getRegisteredActions()) {
        	if (ra == null || ra.getAction() == null) continue;
        	if (msg.length() > 0) msg += "   ";
            msg += ra.getAction().toString();
        }
        return msg;
    }
    
    private void sendStandardErrorFeedback(String actionErrorCondition) {
        final String comments = createActionQueueStack();
        SaveFeedbackAction action = new SaveFeedbackAction(comments, actionErrorCondition, "internal"); 
        CmShared.getCmService().execute(action, new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                CmLogger.info("Feedback saved: " + comments);
            }
            @Override
            public void onFailure(Throwable caught) {
                CmLogger.error("Error saving feedback: " + caught);
            }
        });        
    }
    
    @SuppressWarnings("rawtypes")
    public void requestComplete(RetryAction action) {
        CmLogger.debug("RetryActionManager: requestComplete: " + action);
        _busy = false;
        
        
        /** check for error condition 
         */
        CmLogger.debug("Action class: " + action.getAction());
        if(action.getAction() instanceof GetQuizResultsHtmlAction) {
            sendStandardErrorFeedback(action.getErrorDescription());
        }
        
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
    public Queue<RetryAction> getRegisteredActions() {
    	return _queue;
    }
}

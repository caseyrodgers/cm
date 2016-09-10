package hotmath.gwt.tutor_viewer.client.ui;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.model.SolutionContextsInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionContextsInfoAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

public class ValidateTutorContext extends TutorContextBase {
    

    String pid;
    
    public interface ValidateTutorContextCallback {
        void contextCreated();
        void logMessage(String msg);
    }
    
    public ValidateTutorContext(final String pid, final ValidateTutorContextCallback callBack) {
    	
    	this.pid = pid;

        GetSolutionAction action = new GetSolutionAction(0, 0, pid);
        CmRpcCore.getCmService().execute(action, new AsyncCallback<SolutionInfo>() {
            @Override
            public void onSuccess(SolutionInfo result) {
            	callBack.logMessage("\n\nValidating pid: " + pid);
                validateFeedback(callBack, result.getJs());
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error loading solution", caught);
                caught.printStackTrace();
                
                callBack.logMessage(caught.getMessage());
                
                callBack.contextCreated();
            }
        });
    }

	
	protected void validateFeedback(final ValidateTutorContextCallback callBack, final String currentContext) {
        
        CmRpcCore.getCmService().execute(new GetSolutionContextsInfoAction(pid), new AsyncCallback<SolutionContextsInfo>() {
            @Override
            public void onSuccess(SolutionContextsInfo result) {
            	
            	List<SolutionContext> contexts = result.getContexts();
            	int countMatch=0;
            	int cnt=0;
            	for(SolutionContext c: contexts) {
            		
            		callBack.logMessage("validating context: " + c.getPid() + " -- " + c.getProbNum());
            		
            		String messages = getDifferencesBetweenContexts(currentContext, c.getContextJson());
                    if(messages != null) {
                    	callBack.logMessage(messages);
                    }
                    else {
                    	countMatch++;
                    }
            	}
            	
            	
            	if(countMatch == contexts.size()) {
            		callBack.logMessage(pid + ": All Global Solution Contexts are in sync: " + contexts.size());
            	}
            	else {
            		callBack.logMessage(pid + ": Out of sync contexts: " + (contexts.size() - countMatch) + " out of " + contexts.size());
            	}
            	
            	callBack.contextCreated();
            	
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error loading solution", caught);
                caught.printStackTrace();
                callBack.logMessage(caught.getMessage());
                callBack.contextCreated();
            }
        });
		
	}

	/** return message about diffs, or null if are in sync
	 * 
	 * @param currentContext
	 * @param contextJson
	 * @return
	 */
	protected native String getDifferencesBetweenContexts(String currentContext, String contextJson) /*-{
        return $wnd.__getDiffBetweenContexts(currentContext, contextJson);
    }-*/;
}

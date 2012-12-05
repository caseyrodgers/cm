package hotmath.gwt.tutor_viewer.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GenerateTutorContext {
    
    TutorWrapperPanel _tutorWrapper;
    String pid;
    private GenerateTutorContextCallback callBack;
    
    public interface GenerateTutorContextCallback {
        void contextsCreated(List<String> contexts);
    }
    
    public GenerateTutorContext(final String pid, final String jsonConfig, final GenerateTutorContextCallback callBack) {
        this.pid = pid;
        this.callBack = callBack;
        GetSolutionAction action = new GetSolutionAction(0, 0, pid);
        CmTutor.getCmService().execute(action, new AsyncCallback<SolutionInfo>() {
            @Override
            public void onSuccess(SolutionInfo result) {
                generateContexts(pid, result.getJs(), jsonConfig);
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error loading solution", caught);
                caught.printStackTrace();
                
                callBack.contextsCreated(null);
            }
        });
    }

    List<String> _contexts = new ArrayList<String>();

    /** Call native JS to coreate the context.   Each contact is passed 
     *  back in to gwt_solutionContextCreated and collected and sent back
     *  as a single action.
     *  
     * @param pid
     * @param js
     * @param count
     */
    private void generateContexts(final String pid, String js, String config) {
        Log.info("Creating contexts for: " + pid);
        _contexts.clear();
        
        final int count = extractCountFromConfig(config);
        
        // Collect contexts into _contexts
        _contexts.add(_nativeGenerateContext(pid, js, config));
        for(int i=1;i<count;i++) {
            _contexts.add(_nativeGenerateContext(null,null,null));
        }
        
        callBack.contextsCreated(_contexts);
    }
    
    private int extractCountFromConfig(String config) {
        int start = config.indexOf("limit:") + 7;
        int end=start;
        for(;end<config.length();end++) {
            char c = config.charAt(end);
            if(c == ',' || c == ' ' || c == '}') {
                break;
            }
        }
        String sn = config.substring(start, end);
        return Integer.parseInt(sn);
    }
    
    private native String _nativeGenerateContext(String pid, String js, String jsonConfig) /*-{
    
        $wnd.gwt_solutionHasBeenInitialized = function() {};
        
        var that = this;
        return $wnd.TutorManager.generateContext(pid, js, jsonConfig);
    }-*/;

}

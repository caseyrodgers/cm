package hotmath.gwt.tutor_viewer.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;

public class GenerateTutorContext extends TutorContextBase {
    
    TutorWrapperPanel _tutorWrapper;
    String pid;
    private GenerateTutorContextCallback callBack;
    
    PopupPanel _popup = new PopupPanel();
    FlowPanel _flow;
    
    public interface GenerateTutorContextCallback {
        void contextsCreated(List<String> contexts);
    }
    
    public GenerateTutorContext(final String pid, final String jsonConfig, final GenerateTutorContextCallback callBack) {
        this.pid = pid;
        this.callBack = callBack;
        
        TutorCallbackDefault callback = new TutorCallbackDefault();
        _tutorWrapper = new TutorWrapperPanel(false,  false,false,false, callback);
        _popup.setWidget(_tutorWrapper);
        //_popup.show();
        
        
        GetSolutionAction action = new GetSolutionAction(0, 0, pid);
        CmRpcCore.getCmService().execute(action, new AsyncCallback<SolutionInfo>() {
            @Override
            public void onSuccess(SolutionInfo result) {
                generateContexts(result, pid, result.getJs(), jsonConfig);
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
    private void generateContexts(SolutionInfo solInfo, final String pid, String js, String config) {
        _contexts.clear();
        
        final int count = extractCountFromConfig(config);
        
        // _tutorWrapper.externallyLoadedTutor(solInfo, _popup,null, "", false, false, null);
        
        // Collect contexts into _contexts
        _contexts.add(_nativeGenerateContext(pid, js, config));
        for(int i=1;i<count;i++) {
            _contexts.add(_nativeGenerateContext(null,null,null));
        }
        
        callBack.contextsCreated(_contexts);
    }
    
    /** Evaluate the configuration and extract the limit field
     * 
     * @param config
     * @return
     */
    private native int extractCountFromConfig(String config) /*-{
        var con = eval('(' + config + ')');
        return con.limit;
    }-*/;
}

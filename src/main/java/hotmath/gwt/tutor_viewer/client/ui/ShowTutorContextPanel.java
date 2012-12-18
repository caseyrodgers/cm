package hotmath.gwt.tutor_viewer.client.ui;

import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionContextAction;
import hotmath.gwt.cm_tutor.client.CmTutor;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class ShowTutorContextPanel extends GenerateTutorContextPanel {
    
    public ShowTutorContextPanel() {
        super();
    }
    
    public Widget showAllContexts(final String pid) {
        addLogMessage("Showing all solution contexts for: " + pid);
        GetSolutionContextAction action = new GetSolutionContextAction(pid);
        CmTutor.getCmService().execute(action, new AsyncCallback<CmList<SolutionContext>>() {
            @Override
            public void onSuccess(CmList<SolutionContext> contexts) {
                addViewerLinks(pid, contexts.size());
                
                addLogMessage("contexts shown: " + contexts.size());
            }
            @Override
            public void onFailure(Throwable caught) {
                caught.toString();
                Window.alert(caught.getMessage());
            }
        });
        
        
        return this;
    }

}
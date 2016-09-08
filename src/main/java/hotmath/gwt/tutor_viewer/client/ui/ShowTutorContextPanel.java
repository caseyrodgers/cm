package hotmath.gwt.tutor_viewer.client.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionContextAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class ShowTutorContextPanel extends GenerateTutorContextPanel {
    
    public ShowTutorContextPanel() {
        super();
    }
    
    public Widget showAllContexts(final String pid, boolean shouldGetDetails) {
        addLogMessage("Showing all solution contexts for: " + pid);
        GetSolutionContextAction action = new GetSolutionContextAction(pid);
        action.setLookupDetails(shouldGetDetails);
        CmRpcCore.getCmService().execute(action, new AsyncCallback<CmList<SolutionContext>>() {
            @Override
            public void onSuccess(CmList<SolutionContext> contexts) {
            	
            	VerticalPanel vp = new VerticalPanel();
            	for(SolutionContext context: contexts) {
	                String p = pid + "$" + context.getProbNum();
	                Anchor a = new Anchor(context.getPid() + " --  " + context.getProbNum()  + (context.getCountInUse()>0?" instances: " + context.getCountInUse():""));
	                a.setHref("/tutor_viewer/TutorViewer.html?pid=" + context.getPid());
	                a.setTarget("_blank");
	                vp.add(a);
            	}
            	_mainPanel.add(vp);
                
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

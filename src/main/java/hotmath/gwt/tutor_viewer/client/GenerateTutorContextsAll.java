package hotmath.gwt.tutor_viewer.client;

import hotmath.gwt.cm_rpc.client.rpc.GetSolutionPidsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionPidsAction.PidType;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.tutor_viewer.client.ui.GenerateTutorContext;
import hotmath.gwt.tutor_viewer.client.ui.GenerateTutorContext.GenerateTutorContextCallback;
import hotmath.gwt.tutor_viewer.client.ui.GenerateTutorContextPanel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class GenerateTutorContextsAll extends GenerateTutorContextPanel {

    private int howMany;

    public GenerateTutorContextsAll(int howMany) {
        this.howMany = howMany;
        addLogMessage("Create all solution contexts, maximum: " + howMany);
    }

    int waitTime;

    @Override
    public Widget createContexts(String pidNotUsed) {
        addLogMessage("Updating out of date global contexts");

        GetSolutionPidsAction action = new GetSolutionPidsAction(PidType.OUT_OF_DATE);
        CmRpcCore.getCmService().execute(action, new AsyncCallback<CmList<String>>() {
            @Override
            public void onSuccess(CmList<String> result) {
                
                for(int i=result.size()-1;i>howMany-1;i--) {
                    result.remove(i);
                }
                
                addLogMessage("Creating contexts for " + result.size() + " solutions.");
                createContextsFor(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                caught.toString();
                Window.alert(caught.getMessage());
            }
        });
        return this;
    }

    List<String> _pidsToDo = new ArrayList<String>();

    private void createContextsFor(List<String> pids) {
        _pidsToDo.addAll(pids);

        createContextsForNextPid();
    }

    private void createContextsForNextPid() {
        if (_pidsToDo.size() > 0) {
            final String pid = _pidsToDo.get(0);
            _pidsToDo.remove(0);
            addLogMessage("Creating context for: " + pid);
            new GenerateTutorContext(pid, _jsonConfig, new GenerateTutorContextCallback() {
                @Override
                public void contextsCreated(final List<String> contexts) {

                    if (contexts != null) {
                        saveContexts(pid, contexts);
                    } else {
                        addLogMessage("Error creating contexts for: " + pid);
                    }

                    createContextsForNextPid();
                }
            });
        } else {
            addLogMessage("Finished!");
        }
    }
    @Override
    protected void addViewerLinks(String pid, int count) {
        /** create empty implementation */
    }

}

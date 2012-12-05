package hotmath.gwt.tutor_viewer.client;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionPidsAction;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.tutor_viewer.client.ui.GenerateTutorContext;
import hotmath.gwt.tutor_viewer.client.ui.GenerateTutorContext.GenerateTutorContextCallback;
import hotmath.gwt.tutor_viewer.client.ui.GenerateTutorContextPanel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class GenerateTutorContextsAll extends GenerateTutorContextPanel {
    
    public GenerateTutorContextsAll() {
        addLogMessage("Create all solution contexts");   
    }

    
    @Override
    public Widget createContexts(String book) {
        
        addLogMessage("Creating contexts for books (comma separated): " + book);
        
        GetSolutionPidsAction action = new GetSolutionPidsAction(book);
        CmTutor.getCmService().execute(action, new AsyncCallback<CmList<String>>() {
            @Override
            public void onSuccess(CmList<String> result) {
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
        if(_pidsToDo.size() > 0) {
            final String pid = _pidsToDo.get(0);
            _pidsToDo.remove(0);
            addLogMessage("Creating context for: " + pid);
            
            new GenerateTutorContext(pid,  _jsonConfig, new GenerateTutorContextCallback() {
                @Override
                public void contextsCreated(final List<String> contexts) {
                    
                    if(contexts != null) {
                        saveContexts(pid, contexts);
                    }
                    else {
                        addLogMessage("Error creating contexts for: " + pid);
                    }
                    
                    createContextsForNextPid();
                }
            });
        }
        else {
            addLogMessage("Finished!");   
        }
    }

    protected void addViewerLinks(String pid, int count) {
        /** create empty implementation */
    }
    
}

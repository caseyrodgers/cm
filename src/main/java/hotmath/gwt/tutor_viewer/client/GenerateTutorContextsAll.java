package hotmath.gwt.tutor_viewer.client;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionPidsAction;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.tutor_viewer.client.ui.GenerateTutorContextPanel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class GenerateTutorContextsAll extends GenerateTutorContextPanel {
    
    public GenerateTutorContextsAll(int wait) {
        this.waitTime = wait;
        addLogMessage("Create all solution contexts");   
    }


    int waitTime;
    
    @Override
    public Widget createContexts(String pid) {
        addLogMessage("Creating contexts for pids matching: " + pid + " waitTime: " + waitTime);
        
        GetSolutionPidsAction action = new GetSolutionPidsAction(pid);
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
            
            
            String url = "/tutor_viewer/TutorViewer.html?generate_context=true&pid=" + pid;
            jsniOpenWorkWindow(url);
            new Timer() {
                @Override
                public void run() {
                    createContextsForNextPid();
                }
            }.schedule(waitTime);
        }
        else {
            addLogMessage("Finished!");
            jsniCLoseWOrkWindow();
        }
    }

    native private void jsniOpenWorkWindow(String url) /*-{
       $wnd._workWindow = $wnd.open(url,"CmContext","height=640,width=480,menubar=false,location=false");
    }-*/;


    native private void jsniCLoseWOrkWindow() /*-{
        if($wnd._workWindow) {
            $wnd._workWindow.close();
        }
    }-*/;

    

    @Override
    protected void addViewerLinks(String pid, int count) {
        /** create empty implementation */
    }
    
}

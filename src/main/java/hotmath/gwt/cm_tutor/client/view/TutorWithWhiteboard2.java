package hotmath.gwt.cm_tutor.client.view;


import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkProxy;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class TutorWithWhiteboard2 extends Composite {

    String _testPid = "test_dynamic_practice_addition_1_1$3";

    AssignmentTutorPanel _tutorPanel;
    ShowWorkPanel _showWWork;
    
    int _uid=2755;
    int _assignKey=9;
    AssignmentProblem _assProblem;
    String _pid;

    public TutorWithWhiteboard2() {
        SimplePanel sp = new SimplePanel();

        final DockPanel docPan = new DockPanel();

        _tutorPanel = new AssignmentTutorPanel();
        _tutorPanel.setWidth("400px");
        docPan.add(_tutorPanel,DockPanel.CENTER);

        _showWWork = new ShowWorkPanel(new ShowWorkProxy() {
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return new SaveAssignmentWhiteboardDataAction(_uid,_assignKey, _pid,commandType, data);
            }
            
            @Override
            public void showWorkIsReady() {
                Log.debug("Show Work is ready");
                _tutorPanel.loadSolution(_uid, _assignKey, _testPid);
                loadAssignmentWhiteboardData(_uid, _assignKey, _testPid);
            }
            
            @Override
            public void windowResized() {
                //
            }   
        });
        
        sp.setWidget(_showWWork);
        sp.setWidth("300px");
        docPan.add(sp,DockPanel.EAST);
        
        initWidget(docPan);
    }
    
    
    private void loadAssignmentWhiteboardData(int uid, int assignKey, String pid) {
        
        Log.debug("Load assignment whiteboard: " + uid + ", " + assignKey + ", " + pid);
                
        _uid = uid;
        _assignKey = assignKey;
        _pid = pid;
        
        // always use zero for run_id
        GetAssignmentWhiteboardDataAction action = new GetAssignmentWhiteboardDataAction(uid, pid, assignKey);
        CmTutor.getCmService().execute(action, new AsyncCallback<CmList<WhiteboardCommand>>() {
            public void onSuccess(CmList<WhiteboardCommand> commands) {
                _showWWork.loadWhiteboard(commands);
            }

            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                Log.error("Error getting whiteboard data: " + caught.toString(), caught);
            };
        });
    }    

    
}

package hotmath.gwt.cm_tutor.client.view;


import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentWhiteboardData;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkPanelCallback;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class TutorWithWhiteboardViewer extends Composite {

    //String _testPid = "test_dynamic_practice_addition_1_1$3";
    //String _testPid = "test_dynamic_graphs_1_2_5$1";
    // String _testPid = "alg2ptests3_coursetest_1_algebra2practicetest_1_1";
    // String _testPid = "cmextras_1_1_1_83_1";

    TutorWrapperPanel _tutorPanel;
    ShowWorkPanel _showWWork;
    
    int _uid=2755;
    int _assignKey=9;
    AssignmentProblem _assProblem;
    String _testPid;

    public TutorWithWhiteboardViewer(String pid) {
        
        _testPid = pid;
        SimplePanel sp = new SimplePanel();

        final DockPanel docPan = new DockPanel();

        _tutorPanel = new TutorWrapperPanel(false, false,false, true, new TutorCallbackDefault() {
            @Override
            public void tutorWidgetComplete(String inputValue, boolean correct) {
                processTutorWidgetComplete(inputValue, correct);
            }
            
            @Override
            public void tutorWidgetCompleteDenied(String inputValue, boolean correct) {
                Window.alert("No changes allowed");
            }
        });
        
        //_tutorPanel.setReadOnly(true);
        
        
        _tutorPanel.setWidth("400px");
        docPan.add(_tutorPanel,DockPanel.CENTER);

        _showWWork = new ShowWorkPanel(new ShowWorkPanelCallback() {
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return new SaveAssignmentWhiteboardDataAction(_uid,_assignKey, _testPid,commandType, data, false);
            }
            
            @Override
            public void showWorkIsReady() {
                Log.debug("Show Work is ready");
                loadSolution(_uid, _assignKey, _testPid);
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
    
    
    public void loadSolution(final int uid, final int assignKey, String pid) {

        GetAssignmentSolutionAction action = new GetAssignmentSolutionAction(uid,assignKey, pid);
        CmRpcCore.getCmService().execute(action, new AsyncCallback<AssignmentProblem>() {
            @Override
            public void onSuccess(AssignmentProblem problem) {
                loadSolutionIntoGui(uid, assignKey, problem);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error loading solution", caught);
            }
        });
    }
    

    private void loadSolutionIntoGui(int uid, int assignKey, AssignmentProblem problem) {
        try {
            _uid = uid;
            _assProblem = problem;
            _assignKey = assignKey;
            String resourceTitle = "Problem: " + problem.getInfo().getPid();

            String variableContext = null;

             if(problem.getInfo().getContext() != null) {
                 variableContext = problem.getInfo().getContext().getContextJson();
             }

            InmhItemData item = new InmhItemData(CmResourceType.PRACTICE, problem.getInfo().getPid(), resourceTitle);

            _tutorPanel.externallyLoadedTutor(problem.getInfo(),getWidget(), item.getWidgetJsonArgs(), resourceTitle, true, false, variableContext);
            
            
            if(problem.getLastUserWidgetValue() != null) {
                _tutorPanel.setTutorWidgetValue(problem.getLastUserWidgetValue());
            }
            
        } catch (Exception e) {
            Log.error("Error loading solution into GUI", e);
        }

    }

    private void processTutorWidgetComplete(String inputValue, boolean yesNo) {
        SaveAssignmentTutorInputWidgetAnswerAction action = new SaveAssignmentTutorInputWidgetAnswerAction(_uid, _assignKey,_assProblem.getInfo().getPid(),inputValue,yesNo);
        CmRpcCore.getCmService().execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                Log.debug("Tutor Widget Answer saved to server.");
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error saving tutor widget input value.",caught);
            }
        });
    }
    
    private void loadAssignmentWhiteboardData(int uid, int assignKey, String pid) {
        
        Log.debug("Load assignment whiteboard: " + uid + ", " + assignKey + ", " + pid);
                
        _uid = uid;
        _assignKey = assignKey;
        _testPid = pid;
        
        // always use zero for run_id
        GetAssignmentWhiteboardDataAction action = new GetAssignmentWhiteboardDataAction(uid, pid, assignKey);
        CmRpcCore.getCmService().execute(action, new AsyncCallback<AssignmentWhiteboardData>() {
            public void onSuccess(AssignmentWhiteboardData data) {
                _showWWork.loadWhiteboard(data.getCommands());
            }

            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                Log.error("Error getting whiteboard data: " + caught.toString(), caught);
            };
        });
    }    

    
}

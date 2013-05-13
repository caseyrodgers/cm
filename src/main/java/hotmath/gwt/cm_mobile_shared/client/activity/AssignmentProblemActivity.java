package hotmath.gwt.cm_mobile_shared.client.activity;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.HeaderTitleChangedEvent;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentProblemView;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentWhiteboardData;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCompleteEvent;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCompleteHandler;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AssignmentProblemActivity implements AssignmentProblemView.Presenter {
    
    static private AssignmentProblem __lastProblem;
    static private int __lastAssignKey;
    static private String __lastPid;
    private int assignKey;
    private String pid;
    
    public AssignmentProblemActivity(int assignKey, String pid) {
        this.assignKey = assignKey;
        this.pid = pid;
    }
    
    public void loadProblem(AssignmentProblemView view, AssignmentProblem problem) {
        view.loadProblem(problem);
    }

    @Override
    public void gotoAssignment() {
        History.newItem("assignment:" + __lastAssignKey + ":" + System.currentTimeMillis());
    }

    @Override
    public String getProblemTitle() {
        return (__lastProblem!=null?__lastProblem.getStudentProblem().getStudentLabel():"Problem View");
    }
    
    @Override
    public void fetchProblem(final AssignmentProblemView view, final boolean shouldShowWhiteboard) {
        __lastAssignKey = assignKey;
        __lastPid = pid;
        
        if(__lastProblem != null && __lastProblem.getAssignKey() == assignKey && __lastProblem.getInfo().getPid().equals(pid)) {
            loadProblem(view,  __lastProblem);
            return;
        }
        
        __lastProblem = null;
        final GetAssignmentSolutionAction action = new GetAssignmentSolutionAction(AssignmentData.getUserData().getUid(),assignKey, pid);
        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<AssignmentProblem>() {
            @Override
            public void onSuccess(AssignmentProblem problem) {
                __lastProblem = problem;
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                view.loadProblem(problem);
                
                if(shouldShowWhiteboard) {
                    view.showWhiteboard();
                }
                
                CmRpcCore.EVENT_BUS.fireEvent(new HeaderTitleChangedEvent(problem.getStudentProblem().getStudentLabel()));
            }

            @Override
            public void onFailure(Throwable caught) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                Log.error("Error loading solution: " + action, caught);
                MessageBox.showError("There was a problem talking to the server: " + caught);
            }
        });
    }

    @Override
    public void markSolutionAsComplete() {
        Log.debug("markSolutionAsComplete");
    }

    @Override
    public void newProblem() {
        gotoAssignment();
    }

    @Override
    public void processTutorWidgetComplete(final String inputValue, boolean correct) {
        final AssignmentProblem assProblem = new AssignmentProblem();
        
        if(__lastProblem.isGraded()) {
            MessageBox.showError("This input value will not be saved because the assignment has already been graded.");
            return;
        }

        if(!__lastProblem.isAssignmentClosed()) {
            MessageBox.showError("This input value will not be saved because the assignment is closed.");
            return;
        }
        
        SaveAssignmentTutorInputWidgetAnswerAction action = new SaveAssignmentTutorInputWidgetAnswerAction(AssignmentData.getUserData().getUid(),assignKey,pid,inputValue,correct);
        CmTutor.getCmService().execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                assProblem.setLastUserWidgetValue(inputValue);
                Log.debug("Tutor Widget Answer saved to server.");
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error saving tutor widget input value.",caught);
            }
        });
    }

    AssignmentWhiteboardData __lastWhiteboardData;
    
    @Override
    public void showWhiteboard(final ShowWorkPanel2 showWorkPanel) {
        GetAssignmentWhiteboardDataAction action = new GetAssignmentWhiteboardDataAction(AssignmentData.getUserData().getUid(), pid, assignKey);
        CmTutor.getCmService().execute(action, new AsyncCallback<AssignmentWhiteboardData>() {
            public void onSuccess(final AssignmentWhiteboardData whiteData) {
                __lastWhiteboardData = whiteData;
                
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if(showWorkPanel != null) {
                            showWorkPanel.loadWhiteboard(whiteData.getCommands());                    }
                        }
                });
            }

            public void onFailure(Throwable caught) {
                Log.error("Error getting whiteboard data: " + caught.toString(), caught);
            };
        });
    }

    @Override
    public boolean isAssignmentGraded() {
        return __lastProblem.isGraded();
    }

    @Override
    public InmhItemData getItemData() {
        return new InmhItemData("practice",__lastPid,__lastProblem.getStudentProblem().getStudentLabel());
    }

    @Override
    public void showWorkHasBeenSubmitted() {
        AssignmentShowworkActivity.submitShowWorkToServer(__lastProblem.getAssignKey(),  __lastProblem.getInfo().getPid());
    }
    
    @Override
    public Action<? extends Response> getWhiteboardSaveAction(String pid, CommandType commandType, String data) {
        return new SaveAssignmentWhiteboardDataAction(AssignmentData.getUserData().getUid(),this.assignKey, this.pid, commandType, data, false);        
    }

    static {
        CmRpcCore.EVENT_BUS.addHandler(TutorWidgetInputCompleteEvent.TYPE,  new TutorWidgetInputCompleteHandler() {
            @Override
            public void tutorWidgetComplete(String pid, String inputValue, boolean correct) {
                __lastProblem = null;
            }
        });
    }
    
}

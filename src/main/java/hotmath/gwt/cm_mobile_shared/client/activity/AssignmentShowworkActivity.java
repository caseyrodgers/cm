package hotmath.gwt.cm_mobile_shared.client.activity;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentShowWorkView;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentProblemStatusAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentWhiteboardData;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCompleteEvent;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class AssignmentShowworkActivity implements AssignmentShowWorkView.Presenter{
    
    static private int __lastAssignKey;
    static private String __lastPid;
    private int assignKey;
    private String pid;
    
    AssignmentWhiteboardData __lastWhiteboardData;
    
    public AssignmentShowworkActivity(int assignKey, String pid) {
        __lastAssignKey = assignKey;
        __lastPid = pid;
        this.assignKey = assignKey;
        this.pid = pid;
    }
    
    @Override
    public String getShowWorkTitle() {
        return "Show Work View";
    }

    @Override
    public void gotoTutorView() {
        History.newItem("assignment_problem|" + assignKey + "|" + pid + "|" + System.currentTimeMillis());
    }

    
    static private native String jsni_getProblemStatementFromDocument() /*-{
        var ps = $doc.getElementById('problem_statement');
        if(!ps) {
            return null;
        }
        else {
            return ps.innerHTML;
        }
    }-*/;

    @Override
    public void prepareShowWorkView(final AssignmentShowWorkView showWorkView) {
            GetAssignmentWhiteboardDataAction action = new GetAssignmentWhiteboardDataAction(AssignmentData.getUserData().getUid(), pid, assignKey);
            CmRpcCore.getCmService().execute(action, new AsyncCallback<AssignmentWhiteboardData>() {
                public void onSuccess(final AssignmentWhiteboardData whiteData) {
                    __lastWhiteboardData = whiteData;
                    
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            if(showWorkView != null) {
                                showWorkView.loadWhiteboard(whiteData, whiteData.getProblemStatement());                    }
                            }
                    });
                }

                public void onFailure(Throwable caught) {
                    Log.error("Error getting whiteboard data: " + caught.toString(), caught);
                };
            });
        }
    
    @Override
    public Action<? extends Response> getWhiteboardSaveAction(String pid, CommandType commandType, String commandData) {
        return new SaveAssignmentWhiteboardDataAction(AssignmentData.getUserData().getUid(),this.assignKey, this.pid,commandType, commandData, false);
    }

    
    @Override
    public void showWorkSubmitted() {
        Window.alert("Show Work needs to update list");
//        StudentProblemDto prob = _
//        if (prob != null) {
//            prob.setStatus("Submitted");
//            _studentProblemGrid.getStore().update(prob);
//            saveAssignmentProblemStatusToServer(prob);
//        }        
    }

    @Override
    public void submitShowWork() {
        submitShowWorkToServer(assignKey, pid);
    }
    
    public static void submitShowWorkToServer(int assignKey, final String pid) {
        CmRpcCore.EVENT_BUS.fireEvent(new TutorWidgetInputCompleteEvent(pid, null, false));
        
        SaveAssignmentProblemStatusAction action = new SaveAssignmentProblemStatusAction(AssignmentData.getUserData().getUid(), assignKey, pid, "Submitted");
        CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                Log.info("Showwork submitted");
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error submitting showwork");
            }
        });
        
    }
    
}

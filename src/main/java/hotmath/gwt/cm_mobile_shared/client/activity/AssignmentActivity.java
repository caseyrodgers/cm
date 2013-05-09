package hotmath.gwt.cm_mobile_shared.client.activity;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData.CallbackWhenDataReady;
import hotmath.gwt.cm_mobile_shared.client.util.QuestionBox;
import hotmath.gwt.cm_mobile_shared.client.util.QuestionBox.CallBack;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentView;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc.client.rpc.TurnInAssignmentAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetStudentAssignmentAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCompleteEvent;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCompleteHandler;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AssignmentActivity implements AssignmentView.Presenter {
    
    static private AssignmentActivity __lastInstance;
    public AssignmentActivity() {
        __lastInstance = this;
    }

    static StudentAssignment __lastStudentAssignment;
    AssignmentView _lastView;

    @Override
    public void turnInAssignment(final AssignmentView view) {
        
        QuestionBox.askYesNoQuestion("Turn In Assignment",  "Are you sure you want to turn in this assignment?", new CallBack() {
            @Override
            public void onSelectYes() {

                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
                TurnInAssignmentAction action = new TurnInAssignmentAction(AssignmentData.getUserData().getUid(),__lastStudentAssignment.getAssignment().getAssignKey());
                CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<RpcData>() {
                    @Override
                    public void onSuccess(RpcData result) {
                        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                        refreshAssignment(view);
                        
                        CmRpcCore.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent(TypeOfUpdate.ASSIGNMENTS));
                    }
                    @Override
                    public void onFailure(Throwable caught) {
                        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                    }
                });
            }
        });
        
    }

    @Override
    public void refreshAssignment(AssignmentView view) {
        
        
        int assignKey = __lastStudentAssignment.getAssignment().getAssignKey();
        __lastStudentAssignment=null;
        loadAssignment(view, assignKey);
        CmRpcCore.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent(TypeOfUpdate.ASSIGNMENTS));
    }
    
    private void redrawList() {
        setAssignment(_lastView, __lastStudentAssignment);
    }

    @Override
    public void loadAssignment(final AssignmentView view, final int assignKey) {
        _lastView = view;
        if(__lastStudentAssignment != null && __lastStudentAssignment.getAssignment().getAssignKey() == assignKey) {
            setAssignment(view, __lastStudentAssignment);
            return;
        }
        
        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
        
        AssignmentData.readAssData(new CallbackWhenDataReady() {
            
            @Override
            public void isReady() {
                
                
                GetStudentAssignmentAction action = new GetStudentAssignmentAction(AssignmentData.getUserData().getUid(),assignKey);
                CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<StudentAssignment>() {
                    @Override            
                    public void onSuccess(StudentAssignment result) {
                        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                        setAssignment(view ,result);
                    }
                    @Override
                    public void onFailure(Throwable caught) {
                        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                        Log.debug("Error getting assignment", caught);
                    }            
                });        
                
                
                
            }
        });
    }
    
    

    protected void setAssignment(AssignmentView view, StudentAssignment assignment) {
        __lastStudentAssignment = assignment;
        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
        view.loadAssignment(assignment);
    }




    @Override
    public void showProblem(StudentProblemDto problem) {
        History.newItem("assignment_problem:" + __lastStudentAssignment.getAssignment().getAssignKey() + ":" + problem.getPid() + ":" + System.currentTimeMillis());
    }
    
    
    @Override
    public void gotoAssignmentList() {
        History.newItem("assignment_list:" + System.currentTimeMillis());
    }
    




    static {
        /** Update the current problem's status after a tutor widget
         * has been updated on the server.
         * 
         */
        CmRpcCore.EVENT_BUS.addHandler(TutorWidgetInputCompleteEvent.TYPE,  new TutorWidgetInputCompleteHandler() {
            @Override
            public void tutorWidgetComplete(String pid, String inputValue, boolean correct) {
                boolean changed=false;
                for(StudentProblemDto prob: __lastStudentAssignment.getStudentStatuses().getAssigmentStatuses()) {
                    if(prob.getPid().equals(pid)) {
                        
                        if(!prob.isGraded()) {
                            changed=true;
                            prob.setStatus("Submitted");
                        }
                        else {
                            Log.debug("Not setting problem to submitted because it is already graded");
                        }
                        break;
                    }
                }
                if(changed) {
                    __lastInstance.redrawList();
                }
            }
        });
        
        
        CmRpcCore.EVENT_BUS.addHandler(DataBaseHasBeenUpdatedEvent.TYPE,  new DataBaseHasBeenUpdatedHandler() {
            @Override
            public void databaseUpdated(TypeOfUpdate type) {
                if(type==TypeOfUpdate.ASSIGNMENTS) {
                    __lastStudentAssignment=null;
                }
            }
        });
    }

}

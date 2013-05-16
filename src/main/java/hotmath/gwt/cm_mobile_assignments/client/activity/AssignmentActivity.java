package hotmath.gwt.cm_mobile_assignments.client.activity;


import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.CmMobileAssignments;
import hotmath.gwt.cm_mobile_assignments.client.place.AssignmentPlace;
import hotmath.gwt.cm_mobile_assignments.client.util.AssBusy;
import hotmath.gwt.cm_mobile_assignments.client.util.AssData;
import hotmath.gwt.cm_mobile_assignments.client.view.AssignmentView;
import hotmath.gwt.cm_mobile_assignments.client.view.MainView;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetStudentAssignmentAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCompleteEvent;
import hotmath.gwt.cm_tutor.client.event.TutorWidgetInputCompleteHandler;

import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler.PullActionHandler;

public class AssignmentActivity implements Activity {

    private ClientFactory factory;
    private AssignmentPlace place;
    private List<StudentProblemDto> list = new LinkedList<StudentProblemDto>();
    
    public AssignmentActivity(AssignmentPlace place, ClientFactory factory) {
        this.place = place;
        this.factory = factory;
    }

    @Override
    public String mayStop() {
        return null;
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onStop() {
    }
    
    
    static StudentAssignment __lastStudentAssignment;

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        
        if(__lastStudentAssignment != null && __lastStudentAssignment.getAssignment().getAssignKey() == place.getAssignKey()) {
            setAssignment(panel, __lastStudentAssignment);
            return;
        }
        
        AssBusy.showBusy(true);
        
        getAssignmentInfo(panel);
        
    }
    
   
    
    private void getAssignmentInfo(final AcceptsOneWidget panel) {
        GetStudentAssignmentAction action = new GetStudentAssignmentAction(AssData.getUserData().getUid(),place.getAssignKey());
        CmMobileAssignments.getCmService().execute(action,new AsyncCallback<StudentAssignment>() {
            @Override
            public void onFailure(Throwable caught) {
                AssBusy.showBusy(false);
                Log.debug("Error getting assignment", caught);
            }
            public void onSuccess(StudentAssignment result) {
                setAssignment(panel, result);
            }
        });
    }

    private void setAssignment(AcceptsOneWidget panel, StudentAssignment stuAss) {
        __lastStudentAssignment = stuAss;
        AssBusy.showBusy(false);
        final AssignmentView display = factory.getAssignmentView();
        display.loadAssignment(stuAss);
        
        PullArrowStandardHandler headerHandler = new PullArrowStandardHandler(display.getPullHeader(), display.getPullPanel());
        headerHandler.setErrorText("Error");
        headerHandler.setLoadingText("Refreshing assignment ..");
        headerHandler.setNormalText("pull down to refresh");
        headerHandler.setPulledText("release to load");
        headerHandler.setPullActionHandler(new MyPullAction());
        display.setHeaderPullHandler(headerHandler);
        display.render(stuAss.getStudentStatuses().getAssigmentStatuses());

        String label = stuAss.getAssignment().getAssignmentLabel();
        MainView mainView = factory.getMain(display, "Assignment View", true);
        
        if(panel != null) {
            panel.setWidget(mainView.asWidget());
        }
    }

    class MyPullAction implements PullActionHandler {
        
        public MyPullAction() {
        }

        @Override
        public void onPullAction(final AsyncCallback<Void> callback) {
            new Timer() {
                @Override
                public void run() {
                    getAssignmentInfo(null);
                }
            }.schedule(1000);
        }
    }
    
    
    static {
        
        CmRpcCore.EVENT_BUS.addHandler(TutorWidgetInputCompleteEvent.TYPE,  new TutorWidgetInputCompleteHandler() {
            @Override
            public void tutorWidgetComplete(String pid, String inputValue, boolean correct) {
                for(StudentProblemDto prob: __lastStudentAssignment.getStudentStatuses().getAssigmentStatuses()) {
                    if(prob.getPid().equals(pid)) {
                        updateProblemStatus(prob);
                    }
                }
            }
        });        
        
    }


    protected static void updateProblemStatus(StudentProblemDto prob) {
        prob.setStatus("Submitted");
    }

}

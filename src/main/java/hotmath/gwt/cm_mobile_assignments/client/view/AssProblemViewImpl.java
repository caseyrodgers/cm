package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_mobile_assignments.client.place.AssignmentPlace;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class AssProblemViewImpl extends BaseComposite implements AssProblemView {
    
    ScrollPanel scroll = new ScrollPanel();
    FlowPanel flow = new FlowPanel();
    TutorWrapperPanel tutor;
    private AssignmentProblem problem;
    Presenter presenter;
    public AssProblemViewImpl() {
        
        tutor = new TutorWrapperPanel(true,true,true,true,new TutorCallbackDefault(){
            @Override
            public void solutionHasBeenViewed(String value) {
                presenter.markSolutionAsComplete();
            }
            
            @Override
            public void onNewProblem(int problemNumber) {
                tutorNewProblem();
            }
            
            @Override
            public void tutorWidgetComplete(String inputValue, boolean correct) {
                if(correct) {
                    presenter.markSolutionAsComplete();
                }
            }
            
            @Override
            public void showWhiteboard() {
                presenter.showWhiteboard(getTitle());
            }
            
            @Override
            public boolean showTutorWidgetInfoOnCorrect() {
                return true;
            }
            
            
            @Override
            public Action<RpcData> getSaveSolutionContextAction(String variablesJson, String pid, int problemNumber) {
                int uid=SharedData.getUserInfo().getUid();
                int rid=SharedData.getUserInfo().getRunId();
                return new SaveSolutionContextAction(uid, rid, pid, problemNumber, variablesJson);
            }
            
            @Override
            public Action<UserTutorWidgetStats> getSaveTutorWidgetCompleteAction(String value, boolean yesNo) {
                return new SaveTutorInputWidgetAnswerAction(SharedData.getUserInfo().getUid(), SharedData.getUserInfo().getRunId(),problem.getInfo().getPid(), value, yesNo);
            }
            
            @Override
            public void solutionHasBeenInitialized() {
                if(presenter.getItemData().isViewed()) {
                    tutor.unlockSolution();
                }
            }
            
        });
        //flow.add(tutor);
        scroll.setWidget(tutor);
        initWidget(scroll);
    }
    
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    protected void tutorNewProblem() {
        presenter.newProblem();
    }

    @Override
    public void showProblem(final AssignmentProblem solution) {
        this.problem = solution;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                String context = solution.getInfo().getContext() != null?solution.getInfo().getContext().getContextJson():null;
                tutor.externallyLoadedTutor(problem.getInfo(), (Widget)AssProblemViewImpl.this, problem.getInfo().getPid(),null, problem.getInfo().getJs(), problem.getInfo().getHtml(), problem.getInfo().getPid(), false, false, context);
                tutor.setVisible(true);                
            }
        });
    }
    
    @Override
    public Place getBackPlace() {
        return new AssignmentPlace(problem.getAssignKey());
    }

}

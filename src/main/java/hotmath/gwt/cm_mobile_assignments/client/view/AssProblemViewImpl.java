package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_mobile_assignments.client.place.AssignmentPlace;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel.ButtonBarSetup;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class AssProblemViewImpl extends BaseComposite implements AssProblemView {
    
    //ScrollPanel scroll = new ScrollPanel();
    FlowPanel flow = new FlowPanel();
    TutorWrapperPanel tutor;
    private AssignmentProblem problem;
    Presenter presenter;
    public AssProblemViewImpl() {
        
        tutor = new TutorWrapperPanel(true,true,true,false,new TutorCallbackDefault(){
            
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
                presenter.processTutorWidgetComplete(inputValue, correct);
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
            public boolean moveFirstHintOnWidgetIncorrect() {
                return false;
            }
            
            @Override
            public WidgetStatusIndication indicateWidgetStatus() {
                return !presenter.isAssignmentGraded()?WidgetStatusIndication.INDICATE_SUBMIT_ONLY:WidgetStatusIndication.DEFAULT;
            }
            
            @Override
            public Action<RpcData> getSaveSolutionContextAction(String variablesJson, String pid, int problemNumber) {
                int uid=SharedData.getUserInfo().getUid();
                int rid=SharedData.getUserInfo().getRunId();
                return new SaveSolutionContextAction(uid, rid, pid, problemNumber, variablesJson);
            }

            @Override
            public void solutionHasBeenInitialized() {
                if(presenter.getItemData().isViewed()) {
                    tutor.unlockSolution();
                }
            }
            
        });
        flow.add(tutor);
        //scroll.setWidget(tutor);
        initWidget(flow);
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
                
                tutor.setupButtonBar(new ButtonBarSetup(presenter.isAssignmentGraded(), true, true));
                
                if(problem.getLastUserWidgetValue() != null) {
                    tutor.setTutorWidgetValue(problem.getLastUserWidgetValue());
                }
                
                tutor.setVisible(true);                
            }
        });
    }
    
    @Override
    public Place getBackPlace() {
        return new AssignmentPlace(problem.getAssignKey());
    }

}

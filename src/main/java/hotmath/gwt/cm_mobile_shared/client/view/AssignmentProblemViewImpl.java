package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc_assignments.client.model.ProblemStatus;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel.ButtonBarSetup;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentProblemViewImpl extends Composite implements AssignmentProblemView {

    private Presenter presenter;
    FlowPanel flowPanel;
    private TutorWrapperPanel tutor;
    private AssignmentProblem problem;
    
    public AssignmentProblemViewImpl() {
        flowPanel = new FlowPanel();
        setupInitialPanel();
        initWidget(flowPanel);
    }

    private void setupInitialPanel() {
        flowPanel.clear();
        flowPanel.add(new HTML("Problem loading ..."));
    }
    
    @Override
    public String getViewTitle() {
        return "Assignment Problem";
    }

    @Override
    public String getBackButtonText() {
        return "Back";
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }

    @Override
    public BackAction getBackAction() {
        return new BackAction() {
            @Override
            public boolean goBack() {
                presenter.gotoAssignment();
                return false;
            }
        };
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setPresenter(Presenter listener) {
        this.presenter = listener;
        setupInitialPanel();
        
        presenter.fetchProblem(this);
    }

    @Override
    public void loadProblem(AssignmentProblem problem) {
        this.problem = problem;
        flowPanel.clear();
        
        tutor = new TutorWrapperPanel(true,true,true,false,new TutorCallbackDefault(){
            
            @Override
            public void solutionHasBeenViewed(String value) {
                presenter.markSolutionAsComplete();
            }
            
            @Override
            public void onNewProblem(int problemNumber) {
                presenter.newProblem();
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
            
            @Override
            public void showWorkHasBeenSubmitted() {
                presenter.showWorkHasBeenSubmitted();
            }
            
        });
        flowPanel.add(tutor);
        
        showProblem(problem);
    }
    
    
    private void showProblem(final AssignmentProblem solution) {
        this.problem = solution;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                String context = solution.getInfo().getContext() != null?solution.getInfo().getContext().getContextJson():null;
                tutor.externallyLoadedTutor(problem.getInfo(), (Widget)AssignmentProblemViewImpl.this, problem.getInfo().getPid(),null, problem.getInfo().getJs(), problem.getInfo().getHtml(), problem.getInfo().getPid(), false, false, context);
                
                tutor.setupButtonBar(new ButtonBarSetup(presenter.isAssignmentGraded(), true, true));
                
                if(problem.getLastUserWidgetValue() != null) {
                    tutor.setTutorWidgetValue(problem.getLastUserWidgetValue());
                }
                
                if(problem.getProblemType() ==  ProblemType.WHITEBOARD) {
                    String status = problem.getStatus();
                    if(!status.equals(ProblemStatus.SUBMITTED.toString())) {
                        tutor.jsni_addWhiteboardSubmitButton();
                    }
                    else {
                        TutorWrapperPanel.jsni_showWhiteboardStatus(status);
                    }
                }
                
                tutor.setVisible(true);                
            }
        });
    }

}

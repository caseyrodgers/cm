package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentProblemViewImpl extends Composite implements AssignmentProblemView {

    private Presenter presenter;
    FlowPanel _main;
    private TutorWrapperPanel tutor;
    private AssignmentProblem problem;
    
    public AssignmentProblemViewImpl() {
        FlowPanel flowPanel = new FlowPanel();
        
        SubToolBar subBar = new SubToolBar();
        subBar.add(new SexyButton("View Whiteboard", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showWhiteboard(problem.getStudentProblem().getStudentLabel());
            }
        }));
        flowPanel.add(subBar);
        _main = new FlowPanel();
        flowPanel.add(_main);
        setupInitialPanel();
        initWidget(flowPanel);
    }

    private void setupInitialPanel() {
        _main.clear();
        _main.add(new HTML("Problem loading ..."));
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
        //checkWidget();
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
        _main.clear();
        
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
            public String getSubmitButtonText() {
                return "Submit Answer";
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
        _main.add(tutor);
        
        showProblem(problem);
    }
    
    
    private void showProblem(final AssignmentProblem solution) {
        this.problem = solution;
        Log.debug("Show Problem: " + solution.getInfo().getPid()    );
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                String context = solution.getInfo().getContext() != null?solution.getInfo().getContext().getContextJson():null;
                tutor.externallyLoadedTutor(problem.getInfo(), (Widget)AssignmentProblemViewImpl.this, problem.getInfo().getPid(),null, problem.getInfo().getJs(), problem.getInfo().getHtml(), problem.getInfo().getPid(), false, false, context);
                
                tutor.setupButtonBar(new ButtonBarSetup(presenter.isAssignmentGraded(), true, true));
                
                if(problem.getLastUserWidgetValue() != null) {
                    tutor.setTutorWidgetValue(problem.getLastUserWidgetValue());
                }

                tutor.setVisible(true);    
                
                
                if(problem.getProblemType() ==  ProblemType.WHITEBOARD) {
                    String status = problem.getStatus();
                    if(!status.equals(ProblemStatus.SUBMITTED.toString())) {
                        TutorWrapperPanel.jsni_showWhiteboardWidgetMessage("<div><p>Use the whiteboard to enter your answer</p></div>");
                    }
                    else {
                        TutorWrapperPanel.jsni_showWhiteboardStatus(status);
                    }
                }
            }
        });
    }

    private native void checkWidget() /*-{
        var widgetHolder = $doc.getElementById("hm_flash_widget");
        alert('widget: ' + widgetHolder.innerHTML);
    }-*/;
}

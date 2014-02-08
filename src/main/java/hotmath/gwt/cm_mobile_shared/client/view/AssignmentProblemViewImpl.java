package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_core.client.util.DateUtils4Gwt;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkSubToolBar.Callback;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedHandler;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_assignments.client.model.ProblemStatus;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanelCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel.ButtonBarSetup;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentProblemViewImpl extends Composite implements AssignmentProblemView {

    private static AssignmentProblemViewImpl __lastInstance;
    private Presenter presenter;
    FlowPanel _contentPanel;
    private TutorWrapperPanel tutor;
    private AssignmentProblem problem;
    ShowWorkSubToolBar _subBar;
    
    public AssignmentProblemViewImpl() {
        __lastInstance = this;
        FlowPanel mainPanel = new FlowPanel();

        _subBar = new ShowWorkSubToolBar(true,true, new Callback() {
            
            @Override
            public void whiteboardSubmitted() {
                String errorMessage = AssignmentProblem.canInputValueBeSaved(problem);
                if(errorMessage != null) {
                    PopupMessageBox.showMessage("Whiteboard Not Submitted", errorMessage);
                }
                else {
                     presenter.showWorkHasBeenSubmitted();
                     problem.setStatus("Submitted");
                     tutor.setProblemStatus(problem);
                     hideWhiteboard();
                }
            }
            
            @Override
            public void showWhiteboard() {
                AssignmentProblemViewImpl.this.showWhiteboard();
            }
            
            @Override
            public void hideWhiteboard() {
                AssignmentProblemViewImpl.this.hideWhiteboard();
            }
            
            @Override
            public void showProblem(boolean b) {
                _showWork.setBackground(b);
            }
            
            @Override
            public void showLesson() {
                LessonModel lesson = problem.getStudentProblem().getProblem().getLesson();
                presenter.showLesson(lesson);
            }
        });
        mainPanel.add(_subBar);
        
        _contentPanel = new FlowPanel();
        mainPanel.add(_contentPanel);
        setupInitialPanel();
        initWidget(mainPanel);
    }

    private void hideWhiteboard() {
        _subBar.setupWhiteboardTools(false);
        
        if(_showWork != null) {
            _contentPanel.remove(_showWork);
            _showWork = null;
        }
    }

    private void setupInitialPanel() {
        _contentPanel.clear();
        _contentPanel.add(new HTML("Problem loading ..."));
    }

    @Override
    public String getViewTitle() {
        return problem.getStudentProblem().getStudentLabel() + ", " + DateUtils4Gwt.getPrettyDateString(problem.getAssignmentDueDate(), false);
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
        // checkWidget();
        return this;
    }

    @Override
    public void setPresenter(Presenter listener, boolean shouldShowWhiteboard, CallbackOnComplete callback) {
        
        this.presenter = listener;
        setupInitialPanel();
        
        presenter.fetchProblem(this, shouldShowWhiteboard, callback);
    }

    @Override
    public void loadProblem(AssignmentProblem problem) {
        this.problem = problem;
        problem.getStudentProblem().setHasShowWorkAdmin(false); // turn off blink without server roundtrip.
        _subBar.setupWhiteboardTools(false);
        
        LessonModel l = problem.getStudentProblem().getProblem().getLesson();
        if(l == null || l.getLessonFile().length() == 0) {
            _subBar.preventLessonButton(true);  // override
        }
        else {
            _subBar.preventLessonButton(problem.isPreventLessonAccess()); // use setting on assignment
        }
        _contentPanel.clear();

        tutor = new TutorWrapperPanel(true, true, false, false, new TutorCallbackDefault() {

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
                AssignmentProblem p = AssignmentProblemViewImpl.this.problem;
                String errorMessage = AssignmentProblem.canInputValueBeSaved(p);
                if(errorMessage != null) {
                    PopupMessageBox.showMessage("Input Not Saved", errorMessage);
                }
                else {
                    p.setStatus("Submitted");
                    tutor.setProblemStatus(p);
                    presenter.processTutorWidgetComplete(inputValue, correct);
                }
            }

            @Override
            public void showWhiteboard() {
                AssignmentProblemViewImpl.this.showWhiteboard();
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
                return !presenter.isAssignmentGraded() ? WidgetStatusIndication.NONE : WidgetStatusIndication.DEFAULT;
            }

            @Override
            public Action<RpcData> getSaveSolutionContextAction(String variablesJson, String pid, int problemNumber) {
                int uid = SharedData.getUserInfo().getUid();
                int rid = SharedData.getUserInfo().getRunId();
                return new SaveSolutionContextAction(uid, rid, pid, problemNumber, variablesJson);
            }

            @Override
            public void solutionHasBeenInitialized() {
                if (presenter.getItemData().isViewed()) {
                    tutor.unlockSolution();
                }
                
            }

            @Override
            public void showWorkHasBeenSubmitted() {
                presenter.showWorkHasBeenSubmitted();
            }

        });

        tutor.addStyleName("ass-tutor-panel");
        _contentPanel.add(tutor);

        showProblem(problem);
    }

    /** Remove focus from any text fields on form
     * 
     */
    native protected void jsni_removeFocusFrom() /*-{
        var ae = document.activeElement;
        if(ae) {
            ae.blur();
        }
    }-*/;

    private void showProblem(final AssignmentProblem solution) {
        this.problem = solution;
        Log.debug("Show Problem: " + solution.getInfo().getPid());
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {

                String context = solution.getInfo().getContext() != null ? solution.getInfo().getContext().getContextJson() : null;
                tutor.externallyLoadedTutor(problem.getInfo(), (Widget) AssignmentProblemViewImpl.this, null, problem.getInfo().getPid(), false, false, context);

                tutor.setupButtonBar(new ButtonBarSetup(presenter.isAssignmentGraded(), true, true, "New Problem"));

                if (problem.getLastUserWidgetValue() != null) {
                    tutor.setTutorWidgetValue(problem.getLastUserWidgetValue());
                }
                tutor.setVisible(true);

                setProblemStatus();
            }
        });
    }
    
    private void setProblemStatus() {
        _subBar.showSubmitWhiteboard(false);
        if (problem.getProblemType() == ProblemType.WHITEBOARD) {
            if (!problem.isPastDue() && (!problem.isAssignmentClosed() && !problem.isGraded()) && !problem.getStatus().equals(ProblemStatus.SUBMITTED.toString())) {
                _subBar.showSubmitWhiteboard(true);
            }
        }    
        tutor.setProblemStatus(problem);
    }
    

    ShowWorkPanel2 _showWork;

    @Override
    public void showWhiteboard() {
        
        if(_showWork != null) {
            hideWhiteboard();
        }
        _subBar.setupWhiteboardTools(true);
        
        _showWork = new ShowWorkPanel2(new ShowWorkPanelCallbackDefault() {
            @Override
            public void windowResized() {
            }

            @Override
            public void showWorkIsReady(ShowWorkPanel2 showWork) {
                presenter.showWhiteboard(_showWork);
            }

            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return presenter.getWhiteboardSaveAction(pid, commandType, data);
            }
        });
        _showWork.addStyleName("static_whiteboard");
        _contentPanel.add(_showWork);
        
        _showWork.setBackground(_subBar.getShowProblem());
        
        CmGwtUtils.addDoubleTapRemoveEvent(_showWork.getElement());
        
        // position to top of document so toolbar is visible on open.
        Window.scrollTo(0, 0);

        CmGwtUtils.resizeElement(tutor.getElement());
        
        AssignmentData.removePidFromUnReadTeacherNote(problem.getAssignKey(),problem.getInfo().getPid());
    }
    


    @Override
    public void isNowActive() {
        new Timer() {
            @Override
            public void run() {
                jsni_removeFocusFrom();                
            }
        }.schedule(500);
    }

    protected void alignWhiteboard() {
        if(_showWork != null) {
            CmGwtUtils.resizeElement(tutor.getElement());
        }
    }

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.ASSIGNMENT;        
    }    


    static {
        CmRpcCore.EVENT_BUS.addHandler(WindowHasBeenResizedEvent.TYPE, new WindowHasBeenResizedHandler() {
            @Override
            public void onWindowResized(WindowHasBeenResizedEvent windowHasBeenResizedEvent) {
                __lastInstance.alignWhiteboard();
            }
        });
    }


}

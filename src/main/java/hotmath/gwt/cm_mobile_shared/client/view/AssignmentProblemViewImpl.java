package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_core.client.util.DateUtils4Gwt;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
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
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanel2Callback;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel.ButtonBarSetup;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentProblemViewImpl extends Composite implements AssignmentProblemView {

    private static AssignmentProblemViewImpl __lastInstance;
    private Presenter presenter;
    FlowPanel _contentPanel;
    private TutorWrapperPanel tutor;
    private AssignmentProblem problem;
    FlowPanel whiteboardControlView, whiteboardControlHide;
    SubToolBar _subBar;
    SexyButton _submitWhiteboard;
    private SexyButton _viewWhiteboardButton;
    private Label _problemStatusLabel;
    private SexyButton _toggleBackground;
    
    public AssignmentProblemViewImpl() {
        __lastInstance = this;
        FlowPanel mainPanel = new FlowPanel();

        _subBar = new SubToolBar(true);
        whiteboardControlView = new FlowPanel();
        whiteboardControlHide = new FlowPanel();
        _viewWhiteboardButton = new SexyButton("Whiteboard", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showWhiteboard();
            }
        });
        whiteboardControlView.add(_viewWhiteboardButton);
        
        whiteboardControlView.add(new SexyButton("Lesson", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                LessonModel lesson = problem.getStudentProblem().getProblem().getLesson();
                presenter.showLesson(lesson);
            }
        }));
        
        whiteboardControlHide.add(new SexyButton("Close Whiteboard", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hideWhiteboard();
            }
        }));
        
        
        _toggleBackground = new SexyButton("Hide Problem");
        _toggleBackground.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(_toggleBackground.getText().startsWith("Show")) {
                    _showWork.setBackground(true);
                    _toggleBackground.setButtonText("Hide Problem",null);
                }
                else {
                    _showWork.setBackground(false);
                    _toggleBackground.setButtonText("Show Problem",null);
                }
            }
        });
        whiteboardControlHide.add(_toggleBackground);


        
        _submitWhiteboard = new SexyButton("Submit", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showWorkHasBeenSubmitted();
                _submitWhiteboard.setVisible(false);
                problem.setStatus("Submitted");
                setProblemStatus();
                hideWhiteboard();
            }
        });
        whiteboardControlHide.add(_submitWhiteboard);

        _subBar.add(whiteboardControlView);
        mainPanel.add(_subBar);
        _problemStatusLabel = new Label();
        _problemStatusLabel.addStyleName("ass-prob-status");
        mainPanel.add(_problemStatusLabel);
        
        _contentPanel = new FlowPanel();
        mainPanel.add(_contentPanel);
        setupInitialPanel();
        initWidget(mainPanel);
    }

    protected void setupWhiteboardTools(boolean show) {
        
        if(show) {
            _subBar.remove(whiteboardControlView);
            _subBar.add(whiteboardControlHide);
        }
        else {
            _subBar.remove(whiteboardControlHide);
            _subBar.add(whiteboardControlView);
        }
        
        _subBar.showReturnTo(!show);
    }

    private void hideWhiteboard() {
        setupWhiteboardTools(false);
        
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
        setupWhiteboardTools(false);
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
                presenter.processTutorWidgetComplete(inputValue, correct);
                
                if(_problemStatusLabel.getText().length() == 0) {
                    _problemStatusLabel.setText("Submitted");
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
                tutor.externallyLoadedTutor(problem.getInfo(), (Widget) AssignmentProblemViewImpl.this, problem.getInfo().getPid(), null, problem.getInfo()
                        .getJs(), problem.getInfo().getHtml(), problem.getInfo().getPid(), false, false, context);

                tutor.setupButtonBar(new ButtonBarSetup(presenter.isAssignmentGraded(), true, true));

                if (problem.getLastUserWidgetValue() != null) {
                    tutor.setTutorWidgetValue(problem.getLastUserWidgetValue());
                }
                tutor.setVisible(true);

                _submitWhiteboard.setVisible(false);
                setProblemStatus();
            }
        });
    }
    
    
    private void setProblemStatus() {
        
        
        if (problem.getProblemType() == ProblemType.WHITEBOARD) {
            String status = problem.getStatus();
            if ((!problem.isAssignmentClosed() && !problem.isGraded()) && !status.equals(ProblemStatus.SUBMITTED.toString())) {
                _submitWhiteboard.setVisible(true);
                TutorWrapperPanel.jsni_showWhiteboardWidgetMessage("<div><p>Use the whiteboard to enter your answer</p></div>");
            } else {
                TutorWrapperPanel.jsni_hideWhiteboardStatus();
            }
        }
        
        String msg = "";
        if(problem.isAssignmentClosed() || problem.isGraded()) {
            msg = problem.getStudentProblem().getStatus();
        }
        else {
            String s = problem.getStatus().toLowerCase();
            if(problem.getLastUserWidgetValue() != null || s.equals("submitted") || s.equals("correct") || s.equals("incorrect") || s.equals("half credit")) {
                msg = "Submitted";
            }
            // msg = problem.getStudentProblem().getStatusForStudent();
        }
        //String html = "<div class='ass-prob-status'>" + msg + "</div>";
        _problemStatusLabel.setText(msg);
    }
    

    ShowWorkPanel2 _showWork;

    @Override
    public void showWhiteboard() {
        
        if(_showWork != null) {
            hideWhiteboard();
        }
        setupWhiteboardTools(true);
        
        _showWork = new ShowWorkPanel2(new ShowWorkPanel2Callback() {
            @Override
            public void windowResized() {
            }

            @Override
            public void showWorkIsReady() {
                presenter.showWhiteboard(_showWork);
            }

            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return presenter.getWhiteboardSaveAction(pid, commandType, data);
            }
        });
        _contentPanel.add(_showWork);
        
        _showWork.setBackground(_toggleBackground.getText().startsWith("Hide"));
        
        addDoubleTapRemoveEvent(this, _showWork.getElement());
        
        // position to top of document so toolbar is visible on open.
        Window.scrollTo(0, 0);

        alignWhiteboard();
    }

    /** Use the external Hammer.js library to watch for doubletap
     *  Close the whiteboard once detected.
     * 
     * @param instance
     * @param element
     */
    native private void addDoubleTapRemoveEvent(AssignmentProblemViewImpl instance, Element element) /*-{
        var that = this;
        $wnd.Hammer(element).on("doubletap", function(event) {
            that.@hotmath.gwt.cm_mobile_shared.client.view.AssignmentProblemViewImpl::hideWhiteboard()();
        });
        
        $wnd.Hammer(element).on("pinch", function(event) {
            that.@hotmath.gwt.cm_mobile_shared.client.view.AssignmentProblemViewImpl::hideWhiteboard()();
        });


    }-*/;
    private void alignWhiteboard() {
        
        if (_showWork != null) {
            int width = tutor.getElement().getParentElement().getClientWidth();
            int height = tutor.getElement().getParentElement().getClientWidth();
            
            // height is predetermined in whiteboard.js
            _showWork.getElement().setAttribute("style", "width: " + width + ";height: " + height);  
        }
    }

    static {
        CmRpcCore.EVENT_BUS.addHandler(WindowHasBeenResizedEvent.TYPE, new WindowHasBeenResizedHandler() {
            @Override
            public void onWindowResized(WindowHasBeenResizedEvent windowHasBeenResizedEvent) {
                __lastInstance.alignWhiteboard();
            }
        });
    }

    class MyHorizontalPanel extends HorizontalPanel {
        public MyHorizontalPanel() {
            super.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        }
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

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.ASSIGNMENT;        
    }

}

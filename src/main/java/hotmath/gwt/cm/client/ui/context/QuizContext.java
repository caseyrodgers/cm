package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.ui.CmProgramFlowClientManager;
import hotmath.gwt.cm.client.ui.context.HaveYouCheckedYourWorkWindow.Callback;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.NextPanelInfo;
import hotmath.gwt.cm_tools.client.ui.NextPanelInfoImplDefault;
import hotmath.gwt.cm_tools.client.ui.QuizPage;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Where in the quiz is the user?
 * 
 * @author Casey
 * 
 */
public class QuizContext implements CmContext {

    QuizCmGuiDefinition guiDef;
    String title;
    Text   text;

    public QuizContext(QuizCmGuiDefinition guiDef) {
        this.guiDef = guiDef;
    }

    public int getContextCompletionPercent() {
        return 25;
    }

    public String getContextHelp() {
        return "Try to answer the questions as best you can.  Then click the '>' button to check your answers.";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContextTitle() {
        return getTitle();
    }

    /** Return the quiz context title
     * 
     *  NOTE: the test segments are 1 based.
     *  
     */
    public String getContextSubTitle() {
        String title = getTitle();
        return title + "<h2>Section " + (UserInfo.getInstance().getTestSegment()) + " of "
                + UserInfo.getInstance().getProgramSegmentCount() + "</h2>";
    }

    public NextPanelInfo getNextPanelInfo() {
        return new QuizContextNextPanelInfo();
    }

    public void resetContext() {
    }

    public List<Widget> getTools() {
        List<Widget> list = new ArrayList<Widget>();

        text = new Text();
        text.setText(" ");
        text.addStyleName("cm-main-panel-quiz-text");
        text.setEnabled(true);

        IconButtonWithTooltip btn = new IconButtonWithTooltip("cm-main-panel-next-quiz", "Done taking the quiz");

        btn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
            public void componentSelected(IconButtonEvent ce) {
                ContextController.getInstance().doNext();
            }
        });

        list.add(btn);
        list.add(text);
        text.enable();
        
        return list;
    }

    private void showNextPlacmentQuiz() {
        CatchupMathTools.showAlert("Quiz results", "Good job - we'll now give another quiz.",
                new CmAsyncRequestImplDefault() {
                    public void requestComplete(String requestData) {
                        CatchupMath.getThisInstance().showQuizPanel(-1);
                    }
                });
    }

    private void showQuizResults(CreateTestRunResponse runInfo) {

        if (UserInfo.getInstance().isAutoTestMode()) {
            CmProgramFlowClientManager.getActiveProgramState(new CmProgramFlowClientManager.Callback() {
                
                @Override
                public void programFlow(CmProgramFlowAction flowResponse) {
                    
                    switch(flowResponse.getPlace()) {
                    
                    case PRESCRIPTION:
                        CatchupMath.getThisInstance().showPrescriptionPanel(flowResponse.getPrescriptionResponse());
                        break;
                        
                        
                        default: 
                            CatchupMathTools.showAlert("Invalid Auto Test result: " + flowResponse);
                    }
                }
            });
        } else {
            new QuizCheckResultsWindow(runInfo);
        }
    }

    public void doNext() {
        new HaveYouCheckedYourWorkWindow(new Callback() {
            @Override
            public void quizIsReadyToBeChecked() {
                doCheckTest();
            }
            @Override
            public void quizWasCanceled() {
            }
        });
    }

    public void doCheckTest() {

        /**
         * only issue check test if sure there are no pending question
         * selections. We have to wait until the request is back to client to
         * make sure it has actually been performed on server. Otherwise, we
         * cannot be sure the question has been saved on the server. Then the
         * quiz would be checked incorrectly.
         * 
         */
        if (QuizPage.isAnsweringQuestions()) {
            new Timer() {
                @Override
                public void run() {
                    doCheckTest();
                }
            }.schedule(2000);
            InfoPopupBox.display("Quiz Check", "There are pending question selections ... waiting..");
        } else {
            doCheckTestAux();
        }
    }
    
    /**
     * Perform the actual checking of test on server
     * 
     * TODO: centralize NextAction handling
     */
    public void doCheckTestAux() {
        
        new RetryAction<CreateTestRunResponse>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);

                if (CmShared.getQueryParameter("debug") != null) {
                    InfoPopupBox.display("Quiz Check", "Checking quiz ...");
                }

                CreateTestRunAction action = new CreateTestRunAction(UserInfo.getInstance().getTestId(), UserInfo
                        .getInstance().getUid());

                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CreateTestRunResponse testRunInfo) {
                CmBusyManager.setBusy(false);

                UserInfo.getInstance().setRunId(testRunInfo.getRunId());
                UserInfo.getInstance().setSessionNumber(0); // start
                UserInfo.getInstance().setCorrectPercent(testRunInfo.getTestCorrectPercent());
                
                
                /** check here if SelfPlacement test and handle special
                 * 
                 */
                if(testRunInfo.getNextAction().getPlace() == CmPlace.AUTO_PLACEMENT) {
                    new AutoAdvancedProgramWindow(testRunInfo.getAssignedTest());
                }
                else {
                    showQuizResults(testRunInfo);
                }
            }
            
            public void onFailure(Throwable error) {
                super.onFailure(error);
                
                if(error.getMessage().contains("This test has already been checked")) {
                    CatchupMathTools.showAlert("Already checked", "This test has already been checked.  The current page needs to be reloaded", new CmAsyncRequest() {
                        @Override
                        public void requestFailed(int code, String text) {
                        }
                        
                        @Override
                        public void requestComplete(String requestData) {
                            CmShared.reloadUser();
                        }
                    });
                    
                }
                
            }
        }.register();
    }

    public void doPrevious() {
        CatchupMath.getThisInstance().showLoginPage();
    }

    public String getStatusMessage() {
        if (UserInfo.getInstance().getTestName().toLowerCase().indexOf("auto-enroll") > -1) {
            return "<ul><li><b>Auto-Enroll Quiz</b> You will receive one or more quizzes to place you in a program for review "
                    + "and practice. Work out your answers carefully using our whiteboard or pencil and paper, then press "
                    + "Check Quiz.</li></ul>";
        } else {
            return "<ul><li><b>Relax!</b> Work out each quiz answer at your own pace using our whiteboard or pencil and paper. "
                    + "Press Check Quiz when you are ready to receive review and practice. "
                    + "As you pass the quizzes, you move ahead.</li></ul>";
        }
    }

    public void runAutoTest() {
        doCheckTest();
    }

    /**
     * IconButton that has text tooltip
     * 
     * @author bob
     * 
     */
    class IconButtonWithTooltip extends IconButton {
    	
        public IconButtonWithTooltip(String style, final String tipText) {
            super(style);

            addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
                @Override
                public void handleEvent(BaseEvent be) {

                    text.setText(tipText);
                }
            });
            addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
                @Override
                public void handleEvent(BaseEvent be) {
                	text.setText("");
                }
            });
        }
    }
}

class QuizContextNextPanelInfo extends NextPanelInfoImplDefault {

    public void doNext() {
        CatchupMathTools.showAlert("Do next from the quiz");
    }

    public Widget getNextPanelWidget() {
        LayoutContainer cp = new LayoutContainer();
        cp.setStyleName("quiz-next-panel");
        cp.add(new HTML("When ready, you can "));
        Anchor a = new Anchor("Check Your Test.");
        a.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // History.newItem("pres=0");
                History.newItem("pres=1");
            }
        });
        cp.add(a);
        cp.add(new HTML(
                "<p style='margin-top: 15px;'>After checking your test Catchup Math will assign a personal set of "
                        + "review and practice problems to guide you through " + " your trouble spots.</p>"));
        return cp;
    }


}

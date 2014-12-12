package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.ui.CmProgramFlowClientManager;
import hotmath.gwt.cm.client.ui.context.HaveYouCheckedYourWorkWindow.Callback;
import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.QuizPage;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Where in the quiz is the user?
 * 
 * @author Casey
 * 
 */
public class QuizContext implements CmContext {

    QuizCmGuiDefinition guiDef;
    String title;
    HTML   text;

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

    public void resetContext() {
    }

    public List<Widget> getTools() {
        List<Widget> list = new ArrayList<Widget>();

        text = new HTML();
        text.setText(" ");
        text.addStyleName("cm-main-panel-quiz-text");

        IconButtonWithTooltip btn = new IconButtonWithTooltip("cm-main-panel-next-quiz", "Done taking the quiz");

        btn.addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                // TODO Auto-generated method stub
                ContextController.getInstance().doNext();
            }
        });

        list.add(btn);
        list.add(text);
        
        return list;
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

                if (CmCore.isDebug() == true) {
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
            setToolTip(tipText);
        }
    }
}

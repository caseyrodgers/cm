package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserInfo.UserProgramCompletionAction;
import hotmath.gwt.cm_rpc.client.rpc.AutoAdvanceUserAction;
import hotmath.gwt.cm_rpc.client.rpc.AutoUserAdvanced;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class QuizCheckResultsWindow extends GWindow {

    public QuizCheckResultsWindow(final CreateTestRunResponse runInfo) {
        
        super(false);
        this.setModal(true);
        setPixelSize(350,250);
        setClosable(false);
        setResizable(false);
        setStyleName("auto-assignment-window");
        setHeadingText("Quiz Results");

        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));

        final int correct = runInfo.getCorrect();
        final int total = runInfo.getTotal();

        final int lessonCount = runInfo.getSessionCount();
        String reviewLessons = "<p class='prescription-info'> "
                + "You have <span style='font-size: 120%;font-weight: bold'>" + lessonCount + "</span>  review "
                + (lessonCount == 1 ? "topic" : "topics") + " to study before advancing to the next quiz." + "</p>";

        String msg = "";
        if (UserInfo.getInstance().isCustomProgram()) {
            msg += "<p>Your quiz score: " + "<span class='pass-percent'>" + runInfo.getTestCorrectPercent() + "%"
                    + "</span></p>";

            if (lessonCount > 0) {
                msg += reviewLessons;
            }
        } else if (runInfo.getPassed()) {

            if (runInfo.getNextAction().getPlace() == CmPlace.QUIZ) {
                msg += "<p>Your quiz score: <span class='pass-percent'>" + runInfo.getTestCorrectPercent()
                        + "%</span></p>" + "<p class='info'>You will now be given a quiz for the next section!</p>";
            } else if (correct != total) {
                msg += "<p>Your quiz score: <span class='pass-percent'>" + runInfo.getTestCorrectPercent()
                        + "%</span></p>" + "<p class='pass-congrat'>Congratulations, you passed!</p>" + reviewLessons;
            } else {
                msg += "<p>Your quiz score: <span class='pass-percent'>" + runInfo.getTestCorrectPercent()
                        + "%</span></p>";

                if (UserInfo.getInstance().getOnCompletion() == UserProgramCompletionAction.STOP) {
                    msg += "<p>You have completed this program.</p>";
                } else if (runInfo.getNextAction().getPlace() == CmPlace.AUTO_ADVANCED_PROGRAM) {
                    msg += "<p>You passed this section!  You will now be advanced to the next program.</p>";
                }
            }
        } else {
            // did not pass
            if (runInfo.getNextAction().getPlace() == CmPlace.AUTO_ADVANCED_PROGRAM) {
                msg += "<p>You have been automatically advanced to: " + runInfo.getAssignedTest() + "</p>";
            } else {
                msg += "<p>Your quiz score: <span class='pass-percent'>" + runInfo.getTestCorrectPercent()
                        + "%</span></p>" + "<p>You need to pass: <span class='pass-percent'>"
                        + UserInfo.getInstance().getPassPercentRequired() + "%</span></p>" + reviewLessons;
            }
        }

        HTML html = new HTML(msg);
        this.add(html);

        TextButton close = new TextButton("Continue", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                CmProgramFlowAction nextAction = runInfo.getNextAction();
                switch (nextAction.getPlace()) {
                case PRESCRIPTION:
                    CatchupMath.getThisInstance().showPrescriptionPanel(nextAction.getPrescriptionResponse());
                    break;

                case QUIZ:
                    CatchupMath.getThisInstance().showQuizPanel(nextAction.getQuizResult());
                    break;

                case AUTO_ADVANCED_PROGRAM:
                    /** check for user's advance settings override */
                    if (UserInfo.getInstance().getOnCompletion() == UserProgramCompletionAction.STOP) {
                        CatchupMath.getThisInstance().showEndOfProgramPanel();
                    } else {
                        autoAdvanceUser();
                    }
                    break;

                case END_OF_PROGRAM:
                    CatchupMath.getThisInstance().showEndOfProgramPanel();
                    break;
                }
                QuizCheckResultsWindow.this.close();
            }
        });

        this.addButton(close);
        this.setVisible(true);
    }

    /**
     * Auto Advance the user to the next program
     * 
     */
    static public void autoAdvanceUser() {

        new RetryAction<AutoUserAdvanced>() {
            @Override
            public void attempt() {
                CatchupMathTools.setBusy(true);
                AutoAdvanceUserAction action = new AutoAdvanceUserAction(UserInfo.getInstance().getUid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(AutoUserAdvanced userAdvance) {
                CatchupMathTools.setBusy(false);

                new AutoAdvancedProgramWindow(userAdvance.getProgramTitle());
            }
        }.register();
    }

}

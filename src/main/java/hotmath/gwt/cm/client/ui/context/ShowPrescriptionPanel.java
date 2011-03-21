package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.ui.EndOfProgramWindow;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;

public class ShowPrescriptionPanel extends CmWindow {

    public ShowPrescriptionPanel(final CreateTestRunResponse runInfo) {

        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        this.setModal(true);
        this.setAutoHeight(true);
        this.setWidth(330);
        this.setClosable(false);
        this.setResizable(false);
        this.setStyleName("auto-assignment-window");
        this.setHeading("Quiz Results");

        int correct = runInfo.getCorrect();
        int total = runInfo.getTotal();

        int lessonCount = runInfo.getSessionCount();
        String reviewLessons = "<p class='prescription-info'> " + "You have " + lessonCount + " review "
                + (lessonCount == 1 ? "topic" : "topics") + " to study before advancing to the next quiz." + "</p>";

        String msg = "";
        if(UserInfo.getInstance().isCustomProgram()) {
            msg += "<p>Your quiz score: " +
                   "<span class='pass-percent'>" + 
                      runInfo.getTestCorrectPercent() + "%" +
                   "</span></p>";
            
            if(lessonCount > 0) {
                msg += reviewLessons;
            }
        }
        else if (runInfo.getPassed()) {
            if (correct != total) {
                msg += "<p>Your quiz score: <span class='pass-percent'>" + runInfo.getTestCorrectPercent()
                        + "%</span></p>" + "<p class='pass-congrat'>Congratulations, you passed!</p>" + reviewLessons;
            } else {
                msg += "<p>Your quiz score: <span class='pass-percent'>" + runInfo.getTestCorrectPercent()
                        + "%</span></p>" + "<p class='info'>You will now be given a quiz for the next section!</p>";
            }
        } else {
            // did not pass
            msg += "<p>Your quiz score: <span class='pass-percent'>" + runInfo.getTestCorrectPercent() + "%</span></p>"
                    + "<p>You need to pass: <span class='pass-percent'>"
                    + UserInfo.getInstance().getPassPercentRequired() + "%</span></p>" + reviewLessons;
        }

        Html html = new Html(msg);
        this.add(html);

        Button close = new Button();
        close.setText("Continue");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {

                if (runInfo.getPassed()) {
                    // are there any prescriptions to view?
                    if (runInfo.getSessionCount() > 0) {
                        /** beginning of prescription */
                        UserInfo.getInstance().setSessionNumber(0);
                        CatchupMath.getThisInstance().showPrescriptionPanel();
                    } else {
                        // are there more Quizzes in this program?
                        boolean areMoreSegments = UserInfo.getInstance().getTestSegment() < UserInfo.getInstance()
                                .getTestSegmentCount();
                        if (areMoreSegments) {
                            CatchupMath.getThisInstance().showQuizPanel(-1);
                        } else {
                            switch (UserInfo.getInstance().getOnCompletion()) {

                            case STOP_ALLOW_CONTINUE:
                                new EndOfProgramWindow(true);
                                break;

                            case STOP_DO_NOT_ALLOW_CONTINUE:
                                new EndOfProgramWindow(false);
                                break;

                            case AUTO_ADVANCE:
                                PrescriptionContext.autoAdvanceUser();
                                break;

                            default:
                                CatchupMathTools.showAlert("Unknown onCompletion value: "
                                        + UserInfo.getInstance().getOnCompletion());
                                break;

                            }

                        }
                    }
                } else {
                    UserInfo.getInstance().setSessionNumber(0); // beginning
                    // of
                    // prescription
                    CatchupMath.getThisInstance().showPrescriptionPanel();
                }
                ShowPrescriptionPanel.this.close();
            }
        });

        this.addButton(close);
        this.setVisible(true);
    }
}

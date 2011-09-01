package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.AutoAdvanceUserAction;
import hotmath.gwt.cm_rpc.client.rpc.AutoUserAdvanced;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;

public class QuizCheckResultsWindow extends CmWindow {

    public QuizCheckResultsWindow(final CreateTestRunResponse runInfo) {

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
        String reviewLessons = "<p class='prescription-info'> " + "You have <span style='font-size: 120%;font-weight: bold'>" + lessonCount + "</span>  review "
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
        
            if(runInfo.getNextAction().getPlace() == CmPlace.QUIZ) {
                msg += "<p>Your quiz score: <span class='pass-percent'>" + runInfo.getTestCorrectPercent()
                    + "%</span></p>" + "<p class='info'>You will now be given a quiz for the next section!</p>";
            }
            else if (correct != total) {
                msg += "<p>Your quiz score: <span class='pass-percent'>" + runInfo.getTestCorrectPercent()
                        + "%</span></p>" + "<p class='pass-congrat'>Congratulations, you passed!</p>" + reviewLessons;
            }
            else {
                msg += "<p>Your quiz score: <span class='pass-percent'>" + runInfo.getTestCorrectPercent() + "%</span></p>";
                
                if(runInfo.getNextAction().getPlace() == CmPlace.AUTO_ADVANCED_PROGRAM) {
                    msg += "<p>You passed this section!  You will now be advanced to the next program.</p>";
                }
            }
        } else {
            // did not pass
            if(runInfo.getNextAction().getPlace() == CmPlace.AUTO_ADVANCED_PROGRAM) {
                msg += "<p>You have been automatically advanced to: " + runInfo.getAssignedTest() + "</p>";
            }
            else {
                msg += "<p>Your quiz score: <span class='pass-percent'>" + runInfo.getTestCorrectPercent() + "%</span></p>"
                        + "<p>You need to pass: <span class='pass-percent'>"
                        + UserInfo.getInstance().getPassPercentRequired() + "%</span></p>" + reviewLessons;
            }
        }

        Html html = new Html(msg);
        this.add(html);

        Button close = new Button();
        close.setText("Continue");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {

                CmProgramFlowAction nextAction = runInfo.getNextAction();
                
                switch(nextAction.getPlace()) {
                    case PRESCRIPTION:
                        CatchupMath.getThisInstance().showPrescriptionPanel(nextAction.getPrescriptionResponse());
                        break;
                        
                    case QUIZ:
                        CatchupMath.getThisInstance().showQuizPanel(nextAction.getQuizResult());
                        break;
                      
                    case AUTO_ADVANCED_PROGRAM:
                        autoAdvanceUser();
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
                CmShared.getCmService().execute(action,this);
            }
            @Override
            public void oncapture(AutoUserAdvanced userAdvance) {
                CatchupMathTools.setBusy(false);

                new AutoAdvancedProgramWindow(userAdvance.getProgramTitle());
            }
        }.register();
    }
    
}

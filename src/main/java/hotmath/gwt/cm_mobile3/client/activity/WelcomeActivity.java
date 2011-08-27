package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile3.client.event.ShowPrescriptionLessonViewEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowQuizViewEvent;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;

public class WelcomeActivity implements WelcomeView.Presenter{
    
    private EventBus eventBus;

    public WelcomeActivity(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void prepareView(WelcomeView view) {
        
        CmMobileUser user = CatchupMathMobileShared.getUser();
        
        String firstThing=null;
        CmPlace firstPlace = user.getFlowAction().getPlace();
        if(firstPlace == CmPlace.QUIZ) {
            firstThing = "You will continue from your previous quiz.";
        }
        else if(firstPlace == CmPlace.END_OF_PROGRAM) {
            firstThing = "Your program is complete.";
        }
        else if(firstPlace == CmPlace.AUTO_ADVANCED_PROGRAM) {
            firstThing = "You will be advanced to the next program";
        }
        else if(firstPlace == CmPlace.PRESCRIPTION) {
            firstThing = "You will continue from your previous lesson.";
        }
        else {
            firstThing = "This is your first visit.  You start a new session";
        }
        
        view.prepareView(firstThing);        
    }

    @Override
    public void beginCatchupMath() {
        CmPlace firstPlace = SharedData.getFlowAction().getPlace();
        
        if(firstPlace == null) {
            /** while is firstPlace null?
             */
            if(SharedData.getUserInfo().getRunId() > 0) {
                firstPlace = CmPlace.PRESCRIPTION;
            }
            else if(SharedData.getUserInfo().getTestId() > 0) {
                firstPlace = CmPlace.QUIZ;
            }
            else {
                firstPlace = CmPlace.END_OF_PROGRAM; 
            }
            
        }
        if(firstPlace == CmPlace.QUIZ) {
            eventBus.fireEvent(new ShowQuizViewEvent());
        }
        else if(firstPlace == CmPlace.AUTO_ADVANCED_PROGRAM) {
            Window.alert("Time to auto advance program!");
        }
        else if(firstPlace == CmPlace.END_OF_PROGRAM) {
            Window.alert("End Of Program");
        }
        else if(firstPlace == CmPlace.PRESCRIPTION || SharedData.getUserInfo().getRunId() > 0) {
            eventBus.fireEvent(new ShowPrescriptionLessonViewEvent());
        }
        else {
            if(CatchupMathMobileShared.getUser().getFlowAction().getQuizResult() != null) {
                eventBus.fireEvent(new ShowQuizViewEvent(CatchupMathMobileShared.getUser().getFlowAction().getQuizResult()));
            }
            else {
                MessageBox.showError("Error: unknown first place: " + firstPlace);
            }
        }
    }
}

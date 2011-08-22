package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.event.ShowPrescriptionLessonViewEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowQuizViewEvent;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;

import com.google.gwt.event.shared.EventBus;

public class WelcomeActivity implements WelcomeView.Presenter{
    
    private EventBus eventBus;

    public WelcomeActivity(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void prepareView(WelcomeView view) {
        
        CmMobileUser user = CatchupMathMobileShared.getUser();
        
        String firstThing=null;
        CmPlace firstPlace = user.getFlowAction().getPlace();
        if(firstPlace == null || firstPlace == CmPlace.QUIZ) {
            firstThing = "You will continue from your previous quiz.";
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
        UserLoginResponse userResponse = CatchupMathMobileShared.getUser().getBaseLoginResponse();
        CmPlace firstPlace = userResponse.getNextAction().getPlace();
        if(firstPlace == null || firstPlace == CmPlace.QUIZ) {
            eventBus.fireEvent(new ShowQuizViewEvent());
        }
        else if(firstPlace == CmPlace.PRESCRIPTION || userResponse.getUserInfo().getRunId() > 0) {
            eventBus.fireEvent(new ShowPrescriptionLessonViewEvent());
        }
        else {
            MessageBox.showError("Error: unknown first place: " + firstPlace);
        }
    }
}

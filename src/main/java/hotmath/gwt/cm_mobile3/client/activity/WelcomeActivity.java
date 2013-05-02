package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.event.HandleNextFlowEvent;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData.CallbackWhenDataReady;
import hotmath.gwt.cm_rpc.client.UserInfo;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;

public class WelcomeActivity implements WelcomeView.Presenter{
    
    private EventBus eventBus;
    private ClientFactory factory;

    public WelcomeActivity(ClientFactory factory, EventBus eventBus) {
        this.factory = factory;
        this.eventBus = eventBus;
    }

    public void prepareView(WelcomeView view) {
        
        UserInfo user = SharedData.getMobileUser().getBaseLoginResponse().getUserInfo();
        String firstThing=null;

        if(user.getRunId() > 0){
            firstThing = "<p>You will start this Catchup Math session with a lesson.</p>";
        }
        else {
            firstThing = "<p>You will start this Catchup Math session with a quiz.</p>";
        }
        
        firstThing += "<p>Please work out your answers carefully using our whiteboard or pencil and paper.</p>";
        
        String currentStatus = determineCurrentStatus();
        
        view.prepareView(firstThing, currentStatus);        
    }

    private String determineCurrentStatus() {
        String testName = SharedData.getUserInfo().getTestName();
        UserInfo ui =  SharedData.getUserInfo();
        int runId = ui.getRunId();
        int testId = ui.getTestId();
        int segment = ui.getTestSegment();
        int segmentsTotal = ui.getProgramSegmentCount();
        int lessonNumber = ui.getSessionNumber();
        int lessonsTotal = ui.getSessionCount(); 
        boolean isCustom = ui.isCustomProgram();
        boolean isCustomQuiz = ui.isCustomProgram() && ui.getRunId() == 0;
        
        String status = null;
        if(!isCustomQuiz) {
            String section="";
            if(!isCustom) {
                section = "section " + segment + " of " + segmentsTotal + " of ";
            }
            status = "<p>You are in " + section + " the <b>" + testName + " </b> program.</p>";
            
            if(runId > 0) {
                status += "<p>You have " + lessonsTotal + " lesson" + (lessonsTotal>1?"s":"") +  " to study.";
            }
        }
        else {
            status = "<p>You are in the <b>" + testName + "</b> program.</p>";
        }
        
        return status;
    }
    @Override
    public void beginCatchupMath() {
        eventBus.fireEvent(new HandleNextFlowEvent(SharedData.getMobileUser().getFlowAction()));
    }
    
    
    @Override
    public void beingCatchupMathAssignments() {
        AssignmentData.clear();
        AssignmentData.readAssData(new CallbackWhenDataReady() {
            
            @Override
            public void isReady() {
                History.newItem("assignment_list:" + SharedData.getMobileUser().getUserId() + ":" + System.currentTimeMillis());
            }
        });

    }
}

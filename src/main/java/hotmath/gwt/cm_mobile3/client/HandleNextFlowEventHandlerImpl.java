package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile3.client.event.AutoAdvanceUserEvent;
import hotmath.gwt.cm_mobile3.client.event.HandleNextFlowEventHandler;
import hotmath.gwt.cm_mobile3.client.event.ShowEndOfProgramEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowPrescriptionLessonViewEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowQuizViewEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowWelcomeViewEvent;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.ShowFlashRequiredEvent;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;

public class HandleNextFlowEventHandlerImpl implements HandleNextFlowEventHandler {

    EventBus eventBus;

    public HandleNextFlowEventHandlerImpl(EventBus eventBus) {
        this.eventBus = eventBus;

    }

    @Override
    public void showNextAction(CmProgramFlowAction nextAction) {
        Log.debug("Flow action: " + nextAction);

        CmPlace place = nextAction.getPlace();
        if (place == null) {
            /**
             * while is firstPlace null?
             */
            if (SharedData.getUserInfo().getRunId() > 0) {
                place = CmPlace.PRESCRIPTION;
            } else if (SharedData.getUserInfo().getTestId() > 0) {
                place = CmPlace.QUIZ;
            } else {
                place = CmPlace.END_OF_PROGRAM;
            }
        }

        switch (place) {
        case PRESCRIPTION:
            eventBus.fireEvent(new ShowPrescriptionLessonViewEvent());
            break;

        case QUIZ:
            eventBus.fireEvent(new ShowQuizViewEvent());
            break;

        case WELCOME:
            eventBus.fireEvent(new ShowWelcomeViewEvent());
            break;

        case AUTO_ADVANCED_PROGRAM:
            eventBus.fireEvent(new AutoAdvanceUserEvent(SharedData.getUserInfo().getUid()));
            break;

        case END_OF_PROGRAM:
            eventBus.fireEvent(new ShowEndOfProgramEvent());
            break;

        case AUTO_PLACEMENT:
            MessageBox.showMessage("Auto Placement");
            break;
            
        case ERROR_FLASH_REQUIRED:
            eventBus.fireEvent(new ShowFlashRequiredEvent());
            break;

        default:
            MessageBox.showError("Unknown place: " + nextAction.getPlace());
            break;
        }
    }
}

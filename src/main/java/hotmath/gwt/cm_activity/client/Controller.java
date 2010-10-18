package hotmath.gwt.cm_activity.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;

public class Controller {
    
    static public void navigateToMain() {
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_INITIALIZE));
    }
}

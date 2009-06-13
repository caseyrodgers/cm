package hotmath.gwt.shared.client.eventbus;

import java.util.ArrayList;
import java.util.List;

/** Manages the global eventbus for registering/firing events
 * 
 * @author casey
 *
 */
public class EventBus {
    
    /** Whenever a user is set or changed
     * 
     */
    static public final String EVENT_TYPE_USERCHANGED="USERCHANGED";
    
    /** Whenever the main context is changed
     * 
     */
    static public final String EVENT_TYPE_CONTEXTCHANGED="CONTEXTCHANGED";
    
    
    private static EventBus __instance;
    public static EventBus getInstance() {
        if(__instance == null) {
            __instance = new EventBus();
        }
        return __instance;
    }
    
    
    List<CmEventListener> events = new ArrayList<CmEventListener>();
    private EventBus() {}
 
    public void addEventListener(CmEventListener listener) {
        events.add(listener);
    }
    
    public void removeEventListener(CmEventListener listener) {
        events.remove(listener);
    }
    
    public void fireEvent(CmEvent event) {
        for(int i=0, t=events.size();i<t;i++) {
            CmEventListener l=events.get(i);
            if(l.getEventOfInterest() == null || l.getEventOfInterest().equals(event.getEventName())) {
                l.handleEvent(event);
            }
        }
    }
}

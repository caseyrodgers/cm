package hotmath.gwt.cm_mobile.client.event;

import java.util.ArrayList;
import java.util.List;

/** Manages the global event-bus for 
 * registering/firing events for mobile
 * 
 * Also, there is a main loop that fires once every minute
 * that allows any arbitrary stuff to executed on a recurring
 * basis instead of having multiple timers.
 * 
 * @TODO: how to share across modules
 * 
 * @author casey
 *
 */
public class EventBus {
    
    
    private static EventBus __instance;
    public static EventBus getInstance() {
        if(__instance == null) {
            __instance = new EventBus();
        }
        return __instance;
    }
    
    
    List<CmEventListener> listeners = new ArrayList<CmEventListener>();
    private EventBus() { }
 
    public void addEventListener(CmEventListener listener) {
        listeners.add(listener);
    }
    
    public void removeEventListener(CmEventListener listener) {
        listeners.remove(listener);
    }
    
    /** Fire event named in event.
     * 
     * For each listener, if no specific events
     * of interest are specified  then listener gets
     * all events.  Otherwise, only events of interest
     * are fired.
     *  
     * @param event
     */
    public void fireEvent(CmEvent event) {
        for(int i=0, t=listeners.size();i<t;i++) {
            CmEventListener l=listeners.get(i);
            l.handleEvent(event);
        }
    }
}



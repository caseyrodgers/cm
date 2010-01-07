package hotmath.gwt.shared.client.eventbus;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/** Manages the global eventbus for registering/firing events
 * 
 * Also, there is a main loop that fires once every minute
 * that allows any arbitrary stuff to executed on a recurring
 * basis instead of having multiple timers.
 * 
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
    Timer _mainTimer;
    private EventBus() {
        _mainTimer = new Timer() {
            public void run() {
                /** Deal with obscure bug in IE that adds hash to title
                 * when viewing Flash, if the title has a hash in it.
                 * So, we remove any hash has from title and reset.
                 * 
                 *  This breaks saving bookmarks, but we are not supporting 
                 *  that.  Perhaps, we can have a timer that resets the title
                 *  
                 *  see:  http://bugs.adobe.com/jira/browse/FP-240 
                 */
                String title = Window.getTitle();
                if(title.indexOf("#") > -1) {
                    title = title.substring(0, title.indexOf("#"));
                    Window.setTitle(title);
                }                    
            }
        };
        _mainTimer.scheduleRepeating(1000 * 15);        
    }
 
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



package hotmath.gwt.shared.client.eventbus;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/** Manages the global eventbus for registering/firing events
 * 
 * Also, there is a main loop that fires once every minute
 * that allows any arbitary stuff to executed on a recurring
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
            String types[] = l.getEventsOfInterest();
            if(types == null) {
                l.handleEvent(event);
            }
            else {
                // check requested types, and fire only if requested
                for(int ii=0,tt=types.length;ii<tt;ii++) {
                    if(types[ii].equals(event.getEventName())) {
                        l.handleEvent(event);
                        break;
                    }
                }
            }
        }
    }
    
    
    
    /** Whenever a user is set or changed
     * 
     */
    static public final String EVENT_TYPE_USERCHANGED="USER_CHANGED";
    
    /** Whenever the main context is changed
     * 
     */
    static public final String EVENT_TYPE_CONTEXTCHANGED="CONTEXT_CHANGED";
    
    
    /** When a Modal window is opened
     * 
     */
    static public final String EVENT_TYPE_MODAL_WINDOW_OPEN="MODAL_WINDOW_OPEN";
    
    /** When a Modal window is closed
     * 
     */
    static public final String EVENT_TYPE_MODAL_WINDOW_CLOSED="MODAL_WINDOW_CLOSED";
    
    
    /** Whenever a whiteboard is edited.
     * 
     */
    static public final String EVENT_TYPE_WHITEBOARDUPDATED="WHITEBOAR_DUPDATED";
    
    
    /** After a user's program has been updated/changed
     * 
     * data should contain boolean indicating if change included
     * a change to the user's program or simply a user/password type change
     * 
     */
    static public final String EVENT_TYPE_USER_PROGRAM_CHANGED="USER_PROGRAM_CHANGED";
    
    
    /** Fired whenever a new session topic is set
     * 
     */
    static public final String EVENT_TYPE_TOPIC_CHANGED="TOPIC_CHANGED";
    
    
    /** Fired when the student data needs to be updated
     *  
     */
    static public final String EVENT_TYPE_REFRESH_STUDENT_DATA="REFRESH_STUDENT_DATA";
    
    
    /** Fired everytime a new resource is viewed
     * 
     */
    static public final String EVENT_TYPE_RESOURCE_VIEWER_OPEN="RESOURCE_VIEWER_OPEN";
    
    
    /** Fired everytime a resource viewer is closed/removed
     * 
     */
    static public final String EVENT_TYPE_RESOURCE_VIEWER_CLOSE="RESOURCE_VIEWER_CLOSE";
}

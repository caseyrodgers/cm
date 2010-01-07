package hotmath.gwt.shared.client.eventbus;

/** Represents a single EventBus listener 
 * 
 * @author casey
 *
 */
public interface CmEventListener {
    
    /** Called when an event is fired, the listener can
     * inspect the event and do what is needed.
     * 
     * @param event
     */
    void handleEvent(CmEvent event);
}

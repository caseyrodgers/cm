package hotmath.gwt.cm_mobile_shared.client.event;

public interface CmEventListener {
    
    /** Called when an event is fired, the listener can
     * inspect the event and do what is needed.
     * 
     * @param event
     */
    void handleEvent(CmEvent event);
}
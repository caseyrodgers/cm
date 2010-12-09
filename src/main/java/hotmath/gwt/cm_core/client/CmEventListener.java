package hotmath.gwt.cm_core.client;

public interface CmEventListener {
    
    /** Called when an event is fired, the listener can
     * inspect the event and do what is needed.
     * 
     * @param event
     */
    void handleEvent(CmEvent event);
}
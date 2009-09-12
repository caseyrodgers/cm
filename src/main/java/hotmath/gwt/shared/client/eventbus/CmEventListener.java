package hotmath.gwt.shared.client.eventbus;

public interface CmEventListener {
    
    /** The name of event that we are interested
     *  Null if interested in all 
     *  
     * @return
     */
    String[] getEventsOfInterest();
    void handleEvent(CmEvent event);
}

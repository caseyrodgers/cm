package hotmath.gwt.cm_mobile.client.event;


/** Represents a single event to be fired
 * 
 * @author casey
 *
 */
public class CmEvent {
    String eventName;
    Object eventData;
    EventType eventType;

    public CmEvent(EventType eventType) {
        this.eventType = eventType;
    }
    
    public CmEvent(EventType eventType,Object eventData) {
        this.eventType = eventType;
        this.eventName = eventType.toString();
        this.eventData = eventData;
    }
    public Object getEventData() {
        return eventData;
    }
    public void setEventData(Object eventData) {
        this.eventData = eventData;
    }
    public EventType getEventType() {
        return eventType;
    }
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}


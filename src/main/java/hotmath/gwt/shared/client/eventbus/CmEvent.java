package hotmath.gwt.shared.client.eventbus;


/** Represents a single event to be fired
 * 
 * @author casey
 *
 */
public class CmEvent {
    String eventName;
    Object eventData;
    EventType eventType;
    
    @Deprecated
    public CmEvent(String name) {
        this(name, null);
    }
    
    @Deprecated
    public CmEvent(String name, Object data) {
        this.eventName = name;
        this.eventData = data;
    }

    public CmEvent(EventType eventType) {
        this.eventType = eventType;
    }
    
    public CmEvent(EventType eventType,Object eventData) {
        this.eventType = eventType;
        this.eventName = eventType.toString();
        this.eventData = eventData;
    }

    @Deprecated
    public String getEventName() {
        return eventName;
    }
    
    @Deprecated
    public void setEventName(String eventName) {
        this.eventName = eventName;
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


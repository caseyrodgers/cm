package hotmath.gwt.shared.client.eventbus;

import hotmath.gwt.shared.client.eventbus.EventBus.EventType;

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
    public CmEvent(EventBus.EventType eventType) {
        this.eventType = eventType;
    }
    
    public CmEvent(EventBus.EventType eventType,Object eventData) {
        this.eventType = eventType;
        this.eventName = eventType.toString();
        this.eventData = eventData;
    }
    
    public CmEvent(String name, Object data) {
        this.eventName = name;
        this.eventData = data;
    }
    
    public String getEventName() {
        return eventName;
    }
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

package hotmath.gwt.shared.client.eventbus;

/** Represents a single event to be fired
 * 
 * @author casey
 *
 */
public class CmEvent {
    String eventName;
    Object eventData;
    
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
}

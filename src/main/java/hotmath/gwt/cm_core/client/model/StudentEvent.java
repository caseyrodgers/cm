package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** defines a single event to be sent to a student
 * 
 * @author casey
 *
 */
public class StudentEvent implements Response {
    
    private String eventJson;
    private int uid;
    
    public StudentEvent() {}
    
    public StudentEvent(String eventJson) {
        this.eventJson = eventJson;
    }
    
    
    public StudentEvent(int uid, String eventJson) {
        this.uid = uid;
        this.eventJson = eventJson;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getEventJson() {
        return eventJson;
    }
    
    public void setEventJson(String eventJson) {
        this.eventJson = eventJson;
    };

    
}

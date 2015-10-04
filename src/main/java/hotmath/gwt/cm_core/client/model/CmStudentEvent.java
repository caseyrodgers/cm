package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** defines a single event to be sent to a student
 * 
 * @author casey
 *
 */
public class CmStudentEvent implements Response {
    
    private String eventJson;
    
    public CmStudentEvent() {}
    
    public CmStudentEvent(String eventJson) {
        this.eventJson = eventJson;
    }
    
    
    public String getEventJson() {
        return eventJson;
    }
    
    public void setEventJson(String eventJson) {
        this.eventJson = eventJson;
    };

    
}

package hotmath.gwt.cm_tools.client.model;

/** Class that describes the currently active information 
 *  about a student's current state.
 *  
 *  
 *  All values are defaulted to zero.
 *  
 *  If all values are zero, when user logs
 *  in they will initialize a new program
 *  defined in HA_USER.user_prog_id -> CM_USER_PROGRAM.id 
 *  
 * @author casey
 *
 */
public class StudentActiveInfo {
    
    Integer activeRunId=0;
    Integer activeTestId=0;
    Integer activeSegment=0;
    Integer activeRunSession=0;
    
    
    public Integer getActiveRunId() {
        return activeRunId;
    }
    public void setActiveRunId(Integer activeRunId) {
        this.activeRunId = activeRunId;
    }
    public Integer getActiveTestId() {
        return activeTestId;
    }
    public void setActiveTestId(Integer activeTestId) {
        this.activeTestId = activeTestId;
    }
    public Integer getActiveSegment() {
        return activeSegment;
    }
    public void setActiveSegment(Integer activeSegment) {
        this.activeSegment = activeSegment;
    }
    public Integer getActiveRunSession() {
        return activeRunSession;
    }
    public void setActiveRunSession(Integer activeRunSession) {
        this.activeRunSession = activeRunSession;
    }
    
    
    public String toString() {
        return "StudentActiveInfo [activeRunId=" + activeRunId + ", activeRunSession=" + activeRunSession
                + ", activeSegment=" + activeSegment + ", activeTestId=" + activeTestId + "]";
    }
}

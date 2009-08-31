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
 *   
 *   Each quiz is defined by a test_segment (1-n) and a test_segment_slot
 *   The slot is between 1 and N.  The slot is updated each time the user does not pass a segment.
 *   
 *   Each quiz's solutions are taken from the given slot, so if a user repeats a segment they 
 *   get different questions.
 *  
 * @author casey
 *
 */
public class StudentActiveInfo {
    
    Integer activeRunId=0;
    Integer activeTestId=0;
    Integer activeSegment=0;
    Integer activeSegmentSlot=0;
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
    
    
    /** The quiz's segment slot, identifing which alternate 
     *  questions sets are used.
     *  
     * @return
     */
    public Integer getActiveSegmentSlot() {
        return activeSegmentSlot;
    }
    public void setActiveSegmentSlot(Integer activeSegmentSlot) {
        this.activeSegmentSlot = activeSegmentSlot;
    }
    @Override
    public String toString() {
        return "StudentActiveInfo [activeRunId=" + activeRunId + ", activeRunSession=" + activeRunSession
                + ", activeSegment=" + activeSegment + ", activeSegmentSlot=" + activeSegmentSlot + ", activeTestId="
                + activeTestId + "]";
    }
}

package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

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
 *   Each quiz is defined by a test_segment (0-n) and a test_segment_slot
 *   The slot is between 0 and N.  The slot is updated each time the user does not pass a segment.
 *   
 *   Each quiz's solutions are taken from the given slot, so if a user repeats a segment they 
 *   get different questions.
 *  
 * @author casey
 *
 */
public class StudentActiveInfo implements Response{
    
    int activeRunId=0;
    int activeTestId=0;
    int activeSegment=0;
    int activeSegmentSlot=0;
    int activeRunSession=0;

    CmProgramInfo activeProgram;
    
    public StudentActiveInfo() { /* empty */ }
 
    public CmProgramInfo getActiveProgram() {
        return activeProgram;
    }

    public void setActiveProgram(CmProgramInfo activeProgram) {
        this.activeProgram = activeProgram;
    }

    public void setActiveRunId(int activeRunId) {
        this.activeRunId = activeRunId;
    }

    public void setActiveTestId(int activeTestId) {
        this.activeTestId = activeTestId;
    }

    public void setActiveSegment(int activeSegment) {
        this.activeSegment = activeSegment;
    }

    public void setActiveSegmentSlot(int activeSegmentSlot) {
        this.activeSegmentSlot = activeSegmentSlot;
    }

    public void setActiveRunSession(int activeRunSession) {
        this.activeRunSession = activeRunSession;
    }

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
    
    
    /** The quiz's segment slot, identifying which alternate 
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
        return "StudentActiveInfo [activeRunId=" + activeRunId + ", activeTestId=" + activeTestId + ", activeSegment="
                + activeSegment + ", activeSegmentSlot=" + activeSegmentSlot + ", activeRunSession=" + activeRunSession
                + ", activeProgram=" + activeProgram + "]";
    }
}

package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

/** Represents a single quiz section and the
 * number of times it has been completed
 * 
 * @author casey
 *
 */
public class ProgramSegmentData implements Response {
    Integer segment;
    Integer countCompleted;
    
    public ProgramSegmentData() {}
    
    public ProgramSegmentData(Integer segment, Integer countCompleted) {
        this.segment = segment;
        this.countCompleted = countCompleted;
    }
    
    public Integer getSegment() {
        return segment;
    }
    public void setSegment(Integer segment) {
        this.segment = segment;
    }
    public Integer getCountCompleted() {
        return countCompleted;
    }
    public void setCountCompleted(Integer countCompleted) {
        this.countCompleted = countCompleted;
    }
}

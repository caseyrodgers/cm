package hotmath.gwt.shared.client.model;

import hotmath.gwt.shared.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

/** Represents a single program name and list
 *  of ProgramSegmentData objects that hold count
 *  of assignments for each distinct quiz segment
 *  for the program.
 *  
 * @author casey
 *
 */
public class ProgramData implements Response{
    String programName;
    Integer testDefId;
    List<ProgramSegmentData> segments = new ArrayList<ProgramSegmentData>();
    
    public ProgramData() {}
    
    public ProgramData(String programName, Integer testDefId) {
        this.programName = programName;
        this.testDefId = testDefId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Integer getTestDefId() {
        return testDefId;
    }

    public void setTestDefId(Integer testDefId) {
        this.testDefId = testDefId;
    }
    
    public List<ProgramSegmentData> getSegments() {
        return segments;
    }

    public void setSegments(List<ProgramSegmentData> segments) {
        this.segments = segments;
    }

}

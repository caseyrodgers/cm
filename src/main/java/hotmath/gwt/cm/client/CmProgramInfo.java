package hotmath.gwt.cm.client;

public class CmProgramInfo {
    
    String programName;
    int segmentCount;
    int currentSegment;
    
    
    public CmProgramInfo() {
        programName = "Algebra 1 Proficency Test";
        int segmentCount = 4;
        currentSegment = 1;
    }
    
    public String getProgramName() {
        return programName;
    }
    public void setProgramName(String programName) {
        this.programName = programName;
    }
    public int getSegmentCount() {
        return segmentCount;
    }
    public void setSegmentCount(int segmentCount) {
        this.segmentCount = segmentCount;
    }
    public int getCurrentSegment() {
        return currentSegment;
    }
    public void setCurrentSegment(int currentSegment) {
        this.currentSegment = currentSegment;
    }
}

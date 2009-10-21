package hotmath.gwt.cm_tools.client;

class CmProgramInfo {
    
    String programName;
    int segmentCount;
    int currentSegment;
    
    
    private CmProgramInfo() {
        programName = "Pre-algebra Proficiency";
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

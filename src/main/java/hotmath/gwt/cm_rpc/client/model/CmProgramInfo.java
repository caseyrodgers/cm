package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

@SuppressWarnings("serial")
public class CmProgramInfo implements Response {
    
    int testDefId;
    String programName;
    int segmentCount;
    CmProgramType programType;
    
    public CmProgramInfo() {/* empty */}
    
    public CmProgramInfo(int testDefId, String programName, int programSegmentCount, CmProgramType programType) {
        this.testDefId = testDefId;
        this.programName = programName;
        this.segmentCount = programSegmentCount;
        this.programType = programType;
    }

    public int getTestDefId() {
        return testDefId;
    }

    public void setTestDefId(int testDefId) {
        this.testDefId = testDefId;
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

    public CmProgramType getProgramType() {
		return programType;
	}

	public void setProgramType(CmProgramType programType) {
		this.programType = programType;
	}

    @Override
    public String toString() {
        return "CmProgramInfo [testDefId=" + testDefId + ", programName=" + programName + ", segmentCount="
                + segmentCount + "]";
    }
}

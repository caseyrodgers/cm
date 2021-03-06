package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

@SuppressWarnings("serial")
public class CmProgramInfo implements Response {
    
    int testDefId;
    String programName;
    int segmentCount;
    CmProgramType programType;
    String   subjectId;
    
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

    public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	@Override
    public String toString() {
        return "CmProgramInfo [testDefId=" + testDefId + ", programName=" + programName + ", segmentCount="
                + segmentCount + ", subjectId= " + subjectId + ", programType= " + programType + "]";
    }
}

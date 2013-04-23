package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

@SuppressWarnings("serial")
public class CmProgramAssign implements Response {
    
    CmProgram cmProgram = new CmProgram();
    int id;
    int progSegment;
    int runId;
    int runSession;
    int segmentSlot;
    int testId;
    int userId;
    int userProgId;
    boolean isParallelProg;
    boolean isCurrentMainProg;
    
    public CmProgramAssign() {/* empty */}
    
    public CmProgramAssign(CmProgram cmProg, int userId) {
        this.cmProgram = cmProg;
        this.userId = userId;
    }


	public CmProgram getCmProgram() {
		return cmProgram;
	}

	public void setCmProgram(CmProgram cmProgram) {
		this.cmProgram = cmProgram;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProgSegment() {
		return progSegment;
	}

	public void setProgSegment(int progSegment) {
		this.progSegment = progSegment;
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}

	public int getRunSession() {
		return runSession;
	}

	public void setRunSession(int runSession) {
		this.runSession = runSession;
	}

	public int getSegmentSlot() {
		return segmentSlot;
	}

	public void setSegmentSlot(int segmentSlot) {
		this.segmentSlot = segmentSlot;
	}

	public int getTestId() {
		return testId;
	}

	public void setTestId(int testId) {
		this.testId = testId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserProgId() {
		return userProgId;
	}

	public void setUserProgId(int userProgId) {
		this.userProgId = userProgId;
	}

	public boolean isParallelProg() {
		return isParallelProg;
	}

	public void setParallelProg(boolean isParallelProg) {
		this.isParallelProg = isParallelProg;
	}

	public boolean isCurrentMainProg() {
		return isCurrentMainProg;
	}

	public void setCurrentMainProg(boolean isCurrentMainProg) {
		this.isCurrentMainProg = isCurrentMainProg;
	}

	@Override
    public String toString() {
        return "CmProgramAssign [userId: " + userId + ", id: " + id + ", progSegment: " +
            progSegment + ", runId: " + runId + ", runSession: " + runSession + 
            ", segmentSlot: " + segmentSlot + ", testId: " + testId + ", userProgId: " +
            userProgId + ", isParallelProg: " + isParallelProg + ", isCurrentMainProg: " +
            isCurrentMainProg + ", " + cmProgram.toString() + "]";
    }
}

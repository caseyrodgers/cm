package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

@SuppressWarnings("serial")
public class CmProgramAssign implements Response {
    
    CmProgram cmProgram = new CmProgram();
    int id;
    int progSegment;
    int runId;
    int runSession;
    int testId;
    int userId;
    int userProgId;
    
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

	@Override
    public String toString() {
        return "CmProgramAssign [userId=" + userId + cmProgram.toString() + "]";
    }
}

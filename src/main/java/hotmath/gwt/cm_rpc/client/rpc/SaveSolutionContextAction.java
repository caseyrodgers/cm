package hotmath.gwt.cm_rpc.client.rpc;

public class SaveSolutionContextAction implements Action<RpcData>{
    int uid;
    int runId;
    String pid;
    int problemNumber;
    String contextVariables;
    
    public SaveSolutionContextAction() {}
    
    public SaveSolutionContextAction(int uid, int runId, String pid, int problemNumber, String contextVariables) {
        this.uid = uid;
        this.runId = runId;
        this.pid = pid;
        this.problemNumber = problemNumber;
        this.contextVariables = contextVariables;
    }

    public int getProblemNumber() {
        return problemNumber;
    }

    public void setProblemNumber(int problemNumber) {
        this.problemNumber = problemNumber;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public String getContextVariables() {
        return contextVariables;
    }

    public void setContextVariables(String contextVariables) {
        this.contextVariables = contextVariables;
    }

    @Override
    public String toString() {
        return "SaveSolutionContextAction [uid=" + uid + ", runId=" + runId + ", pid=" + pid + ", problemNumber="
                + problemNumber + ", contextVariables=" + (contextVariables!=null?contextVariables.length():0) + "]";
    }
    

}

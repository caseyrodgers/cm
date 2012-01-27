package hotmath.gwt.cm_rpc.client.rpc;

public class MarkHomeWorkAttemptedAction implements Action<RpcData> {
    
    public MarkHomeWorkAttemptedAction() {};
    
    int runId;
    String eppTitle;    
    String pid;
    
    public MarkHomeWorkAttemptedAction(int runId, String eppTitle, String pid) {
        this.runId = runId;
        this.pid = pid;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public String getEppTitle() {
        return eppTitle;
    }

    public void setEppTitle(String eppTitle) {
        this.eppTitle = eppTitle;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "MarkHomeWorkCompletedAction [runId=" + runId + ", eppTitle=" + eppTitle + ", pid=" + pid + "]";
    }
    
}

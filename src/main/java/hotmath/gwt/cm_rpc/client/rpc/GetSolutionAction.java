package hotmath.gwt.cm_rpc.client.rpc;


/** Return solution information for a given user and pid
 * 
 * @author casey
 *
 */
public class GetSolutionAction extends ActionBase implements Action<SolutionInfo> {
    
    String pid;
    int runId;
    int uid;

    public GetSolutionAction() {}
    
    public GetSolutionAction(int uid, int runId, String pid) {
        this.uid = uid;
        this.runId = runId;
        this.pid = pid;
        
        setActionInfo(new ActionInfo(ActionType.STUDENT));
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
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

    @Override
    public String toString() {
        return "GetSolutionAction [pid=" + pid + ", runId=" + runId + ", uid=" + uid + "]";
    }

}

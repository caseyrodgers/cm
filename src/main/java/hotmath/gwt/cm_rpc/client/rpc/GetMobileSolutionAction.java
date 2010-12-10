package hotmath.gwt.cm_rpc.client.rpc;

public class GetMobileSolutionAction implements Action<SolutionResponse>{
    String pid;
    int uid;
    
    public GetMobileSolutionAction() { }
    
    public GetMobileSolutionAction(int uid, String pid) {
        this.uid = uid;
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "GetMobileSolutionAction [pid=" + pid + ", uid=" + uid + "]";
    }
}

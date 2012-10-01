package hotmath.gwt.cm_rpc.client.rpc;


public class SaveAssignmentProblemStatusAction implements Action<RpcData>{
    
    private int uid;
    private int assignKey;
    private String pid;
    String status;

    public SaveAssignmentProblemStatusAction(){}

    public SaveAssignmentProblemStatusAction(int uid, int assignKey, String pid, String status) {
        this.uid = uid;
        this.assignKey = assignKey;
        this.pid = pid;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    
}

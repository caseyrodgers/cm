package hotmath.gwt.cm_rpc.client.rpc;


/** Return solution information for a given user and pid
 * 
 * @author casey
 *
 */
public class GetAssignmentSolutionAction extends ActionBase implements Action<SolutionInfo> {
    
    String pid;
    int uid;
    int assignKey;

    public GetAssignmentSolutionAction() {}
    
    public GetAssignmentSolutionAction(int uid, int assignKey, String pid) {
        this.uid = uid;
        this.assignKey = assignKey;
        this.pid = pid;
        
        setActionInfo(new ActionInfo(ActionType.STUDENT));
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

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    @Override
    public String toString() {
        return "GetAssignmentSolutionAction [pid=" + pid + ", uid=" + uid + ", assignKey=" + assignKey + "]";
    }
    
    
}

package hotmath.gwt.cm_rpc.client.rpc;


/** Return solution information for a given user and pid
 * 
 * @author casey
 *
 */
public class GetSolutionAction extends ActionBase implements Action<SolutionInfo> {
    
    String pid;
    int uid;

    public GetSolutionAction() {}
    
    public GetSolutionAction(int uid, String pid) {
        this.uid = uid;
        this.pid = pid;
        
        setActionInfo(new ActionInfo(ActionType.STUDENT));
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
        return "GetSolutionAction [pid=" + pid + ", uid=" + uid + "]";
    }

}

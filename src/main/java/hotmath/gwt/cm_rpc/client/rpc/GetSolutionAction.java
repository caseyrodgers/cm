package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

/** Return solution information for a given user and pid
 * 
 * @author casey
 *
 */
public class GetSolutionAction implements Action<RpcData> {
    
    String pid;
    int uid;

    public GetSolutionAction() {}
    
    public GetSolutionAction(int uid, String pid) {
        this.uid = uid;
        this.pid = pid;
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

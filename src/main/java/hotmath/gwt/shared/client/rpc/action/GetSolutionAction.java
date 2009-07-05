package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

public class GetSolutionAction implements Action<RpcData>{
    
    String pid;
    int uid;
    
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

    public GetSolutionAction(int uid, String pid) {
        this.uid = uid;
        this.pid = pid;
    }

}

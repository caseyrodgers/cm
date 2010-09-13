package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActionBase;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

/** Save a single Whiteboard command for this user
 * 
 * @author casey
 *
 */
public class ClearWhiteboardDataAction extends ActionBase implements Action<RpcData> {

    Integer uid;
    Integer rid;
    String pid;
    
    public ClearWhiteboardDataAction() {
        getClientInfo().setUserType(UserType.STUDENT);    	
    }
        
    public ClearWhiteboardDataAction(int uid, int runId, String pid) {
        this.uid = uid;
        this.rid = runId;
        this.pid = pid;
        
        getClientInfo().setUserId(uid);
        getClientInfo().setUserType(UserType.STUDENT);
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}

package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;


/** Get a single user's widget stats
 * 
 * @author casey
 *
 */
public class GetUserInfoStatsAction implements Action<UserInfoStats>{
    
    private int uid;

    public GetUserInfoStatsAction(){}
    
    public GetUserInfoStatsAction(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "GetUserInfoStatsAction [uid=" + uid + "]";
    }
}

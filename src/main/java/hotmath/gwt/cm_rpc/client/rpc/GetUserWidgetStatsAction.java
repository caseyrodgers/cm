package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;


/** Get a single user's widget stats
 * 
 * @author casey
 *
 */
public class GetUserWidgetStatsAction implements Action<UserTutorWidgetStats>{
    
    private int uid;

    public GetUserWidgetStatsAction(){}
    
    public GetUserWidgetStatsAction(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
    

}

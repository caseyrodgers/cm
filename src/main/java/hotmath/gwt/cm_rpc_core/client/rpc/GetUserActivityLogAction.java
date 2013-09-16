package hotmath.gwt.cm_rpc_core.client.rpc;

import hotmath.gwt.cm_tools.client.model.ActivityLogRecord;

public class GetUserActivityLogAction implements Action<CmList<ActivityLogRecord>>{
    private int uid;
    public GetUserActivityLogAction() {}
    public GetUserActivityLogAction(int uid) {
        this.uid = uid;
    }
    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
}

package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class ResetUserPrescripionLessonAction implements Action<RpcData>{
    
    private int userId;

    public ResetUserPrescripionLessonAction() {}
    
    public ResetUserPrescripionLessonAction(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}

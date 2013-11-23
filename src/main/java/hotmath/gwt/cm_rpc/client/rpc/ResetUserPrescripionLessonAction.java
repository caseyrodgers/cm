package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class ResetUserPrescripionLessonAction implements Action<RpcData>{
    
    private int userId;
    private String pidInLesson;

    public ResetUserPrescripionLessonAction() {}
    
    public ResetUserPrescripionLessonAction(int userId, String pidInLesson) {
        this.userId = userId;
        this.pidInLesson = pidInLesson;
    }

    public String getPidInLesson() {
        return pidInLesson;
    }

    public void setPidInLesson(String pidInLesson) {
        this.pidInLesson = pidInLesson;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ResetUserPrescripionLessonAction [userId=" + userId + ", pidInLesson=" + pidInLesson + "]";
    }

}

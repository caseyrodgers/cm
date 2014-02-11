package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class SaveCustomProblemLinkedLessonAction implements Action<RpcData>{
    
    private int adminId;
    private String pid;
    private CmList<LessonModel> lessons;

    public SaveCustomProblemLinkedLessonAction() {}
    
    public SaveCustomProblemLinkedLessonAction(int adminId, String pid, CmList<LessonModel> lessons) {
        this.adminId = adminId;
        this.pid = pid;
        this.lessons = lessons;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public CmList<LessonModel> getLessons() {
        return lessons;
    }

    public void setLessons(CmList<LessonModel> lessons) {
        this.lessons = lessons;
    }

}

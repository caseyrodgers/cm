package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetCustomProblemLinkedLessonAction implements Action<CmList<LessonModel>>{
    
    private String pid;

    public GetCustomProblemLinkedLessonAction(){}
    
    public GetCustomProblemLinkedLessonAction(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

}

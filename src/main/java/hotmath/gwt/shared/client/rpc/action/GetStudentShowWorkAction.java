package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;

public class GetStudentShowWorkAction implements Action<CmList<StudentShowWorkModel>>{
    
    Integer uid;
    Integer runId;
    
    public GetStudentShowWorkAction() {}
    
    public GetStudentShowWorkAction(Integer uid, Integer runId) {
        this.uid = uid;
        this.runId = runId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getRunId() {
        return runId;
    }

    public void setRunId(Integer runId) {
        this.runId = runId;
    }
}

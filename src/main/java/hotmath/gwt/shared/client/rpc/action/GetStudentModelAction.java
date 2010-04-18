package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StudentModelI;

public class GetStudentModelAction implements Action<StudentModelI> {
    
    Integer uid;
    
    public GetStudentModelAction() {}
    
    public GetStudentModelAction(Integer uid) {
        this.uid = uid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }
}

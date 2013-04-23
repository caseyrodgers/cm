package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.GradeBookModel;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;

public class GetGradeBookDataAction implements Action<CmList<GradeBookModel>> {
    
    GetStudentGridPageAction studentGridAction;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    int uid;
    public GetGradeBookDataAction(){}
    
    public GetGradeBookDataAction(int uid) {
        this.uid = uid;
    }

    public GetStudentGridPageAction getStudentGridAction() {
        return studentGridAction;
    }

    public void setStudentGridAction(GetStudentGridPageAction studentGridAction) {
        this.studentGridAction = studentGridAction;
    }
}

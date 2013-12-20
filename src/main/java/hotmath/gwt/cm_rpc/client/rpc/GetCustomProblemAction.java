package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetCustomProblemAction implements Action<CmList<CustomProblemModel>> {

    private TeacherIdentity teacher;
    public GetCustomProblemAction() {}
    public GetCustomProblemAction(TeacherIdentity teacher) {
        this.teacher = teacher;
    }
    public TeacherIdentity getTeacher() {
        return teacher;
    }
    public void setTeacher(TeacherIdentity teacher) {
        this.teacher = teacher;
    }
}

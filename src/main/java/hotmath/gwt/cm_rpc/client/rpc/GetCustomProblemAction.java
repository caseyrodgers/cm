package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.model.CustomProblemInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetCustomProblemAction implements Action<CustomProblemInfo> {

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

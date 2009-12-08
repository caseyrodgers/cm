package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.Action;

public class GetStudentActivityAction implements Action<CmList<StudentActivityModel>>{

    StudentModelI student;
    
    public GetStudentActivityAction(){}
    
    public GetStudentActivityAction(StudentModelI sm) {
        this.student = sm;
    }

    public StudentModelI getStudent() {
        return student;
    }

    public void setStudent(StudentModelI student) {
        this.student = student;
    }
}

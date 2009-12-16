package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.Action;

public class AddStudentAction implements Action<StudentModelI> {

    StudentModelI student;
    
    public AddStudentAction() {
    }
    
    public AddStudentAction(StudentModelI sm) {
        this.student = sm;
    }
    
    public StudentModelI getStudent() {
        return student;
    }
    public void setStudent(StudentModelI student) {
        this.student = student;
    }

}

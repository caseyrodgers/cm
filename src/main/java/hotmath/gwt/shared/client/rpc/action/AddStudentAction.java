package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;

public class AddStudentAction implements Action<StudentModel> {
    
    public StudentModel getStudent() {
        return student;
    }
    public void setStudent(StudentModel student) {
        this.student = student;
    }
    StudentModel student;
    public AddStudentAction(StudentModel sm) {
        this.student = sm;
    }
}

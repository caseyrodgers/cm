package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;

public class UpdateStudentAction implements Action<StudentModel>{
    
    StudentModel student;
    Boolean stuChanged;
    Boolean progChanged;
    Boolean progIsNew;
    Boolean passcodeChanged;
    
    public UpdateStudentAction(StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew, Boolean passcodeChanged) {
        this.student = sm;
        this.stuChanged = stuChanged;
        this.progChanged = progChanged;
        this.progIsNew = progIsNew;
        this.passcodeChanged = passcodeChanged;
    }

    @Override
    public String toString() {
        return "UpdateStudentAction [passcodeChanged=" + passcodeChanged + ", progChanged=" + progChanged
                + ", progIsNew=" + progIsNew + ", stuChanged=" + stuChanged + ", student=" + student + "]";
    }

    public StudentModel getStudent() {
        return student;
    }

    public void setStudent(StudentModel student) {
        this.student = student;
    }

    public Boolean getStuChanged() {
        return stuChanged;
    }

    public void setStuChanged(Boolean stuChanged) {
        this.stuChanged = stuChanged;
    }

    public Boolean getProgChanged() {
        return progChanged;
    }

    public void setProgChanged(Boolean progChanged) {
        this.progChanged = progChanged;
    }

    public Boolean getProgIsNew() {
        return progIsNew;
    }

    public void setProgIsNew(Boolean progIsNew) {
        this.progIsNew = progIsNew;
    }

    public Boolean getPasscodeChanged() {
        return passcodeChanged;
    }

    public void setPasscodeChanged(Boolean passcodeChanged) {
        this.passcodeChanged = passcodeChanged;
    }

}

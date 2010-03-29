package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.Action;

public class UpdateStudentAction implements Action<StudentModelI> {
    
	private static final long serialVersionUID = 728083490224703003L;
	
	StudentModelI student;
    Boolean stuChanged;
    Boolean progChanged;
    Boolean progIsNew;
    Boolean passcodeChanged;
    Boolean passPercentChanged;
    
    public UpdateStudentAction() {}
    
    public UpdateStudentAction(StudentModelI sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew, Boolean passcodeChanged,
    		Boolean passPercentChanged) {
        this.student = sm;
        this.stuChanged = stuChanged;
        this.progChanged = progChanged;
        this.progIsNew = progIsNew;
        this.passcodeChanged = passcodeChanged;
        this.passPercentChanged = passPercentChanged;
    }

    @Override
    public String toString() {
        return "UpdateStudentAction [passcodeChanged=" + passcodeChanged + ", progChanged=" + progChanged
                + ", progIsNew=" + progIsNew + ", stuChanged=" + stuChanged + ", passPercent=" + passPercentChanged 
                + ", student=" + student + "]";
    }

    public StudentModelI getStudent() {
        return student;
    }

    public void setStudent(StudentModelI student) {
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

    public Boolean getPassPercentChanged() {
        return passPercentChanged;
    }

    public void setPassPercentChanged(Boolean passPercentChanged) {
        this.passPercentChanged = passPercentChanged;
    }

}

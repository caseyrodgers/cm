package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StudentModelI;

public class UpdateStudentAction implements Action<StudentModelI> {
    
	private static final long serialVersionUID = 728083490224703003L;
	
	StudentModelI student;
    Boolean stuChanged;
    Boolean progChanged;
    Boolean progIsNew;
    Boolean passcodeChanged;
    Boolean passPercentChanged;
    Boolean sectionNumChanged;
    
    public UpdateStudentAction() {}
    
    public UpdateStudentAction(StudentModelI sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew, Boolean passcodeChanged,
    		Boolean passPercentChanged, Boolean sectionNumChanged) {
        this.student = sm;
        this.stuChanged = stuChanged;
        this.progChanged = progChanged;
        this.progIsNew = progIsNew;
        this.passcodeChanged = passcodeChanged;
        this.passPercentChanged = passPercentChanged;
        this.sectionNumChanged = sectionNumChanged;
    }

    @Override
    public String toString() {
        return "UpdateStudentAction [passcodeChanged=" + passcodeChanged + ", progChanged=" + progChanged
                + ", progIsNew=" + progIsNew + ", stuChanged=" + stuChanged + ", passPercentChanged=" + passPercentChanged 
                + ", sectionNumChanged=" + sectionNumChanged + ", student=" + student + "]";
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

	public Boolean getSectionNumChanged() {
		return sectionNumChanged;
	}

	public void setSectionNumChanged(Boolean sectionNumChanged) {
		this.sectionNumChanged = sectionNumChanged;
	}

}

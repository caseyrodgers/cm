package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;



public class CustomProblemFolderNode extends FolderDto {
    
    TeacherIdentity teacher;
    
	public CustomProblemFolderNode(String name, TeacherIdentity teacher) {
		super(__nextId(), name);
		this.teacher = teacher;
	}

	public String getFolderName() {
		return getName();
	}

    public TeacherIdentity getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherIdentity teacher) {
        this.teacher = teacher;
    }
}

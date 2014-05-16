package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class DeleteCustomProblemTreePathAction implements Action<RpcData>{
	
	private TeacherIdentity teacher;
	private String path;

	public DeleteCustomProblemTreePathAction() {}
	
	public DeleteCustomProblemTreePathAction(TeacherIdentity teacher, String path) {
		this.teacher = teacher;
		this.path = path;
	}



	public TeacherIdentity getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherIdentity teacher) {
		this.teacher = teacher;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}


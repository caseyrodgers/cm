package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class AddCustomProblemTreePathAction implements Action<RpcData>{
	
	TeacherIdentity teacher;
	private String treePath;

	public AddCustomProblemTreePathAction(){}
	
	public AddCustomProblemTreePathAction(TeacherIdentity teacher, String treePath) {
		this.teacher = teacher;
		this.treePath = treePath;
	}

	public TeacherIdentity getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherIdentity teacher) {
		this.teacher = teacher;
	}

	public String getTreePath() {
		return treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

}

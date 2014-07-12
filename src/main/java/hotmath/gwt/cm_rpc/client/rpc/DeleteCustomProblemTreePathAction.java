package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class DeleteCustomProblemTreePathAction implements Action<RpcData>{
	
	private TeacherIdentity teacher;
	private String path;
	boolean teacherNode;

	public DeleteCustomProblemTreePathAction() {}
	
	public DeleteCustomProblemTreePathAction(TeacherIdentity teacher, String path, boolean isTeacherNode) {
		this.teacher = teacher;
		this.teacherNode = isTeacherNode;
		this.path = path;
	}

	public boolean isTeacherNode() {
		return teacherNode;
	}

	public void setTeacherNode(boolean teacherNode) {
		this.teacherNode = teacherNode;
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

    @Override
    public String toString() {
        return "DeleteCustomProblemTreePathAction [teacher=" + teacher + ", path=" + path + ", teacherNode="
                + teacherNode + "]";
    }
}


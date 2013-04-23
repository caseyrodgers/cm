package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.model.CmStudentPagingLoadResult;

import java.util.List;

public class GetStudentGridPageExtendedAction implements Action<CmStudentPagingLoadResult<StudentModelExt>>{
	
	List<Integer> studentUids;
	int adminId;
	
    public GetStudentGridPageExtendedAction() {}

    public GetStudentGridPageExtendedAction(int adminId, List<Integer> studentUids) {
		this.adminId = adminId;
		this.studentUids = studentUids;
	}

	public List<Integer> getStudentUids() {
		return studentUids;
	}

	public void setStudentUids(List<Integer> studentUids) {
		this.studentUids = studentUids;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}
}

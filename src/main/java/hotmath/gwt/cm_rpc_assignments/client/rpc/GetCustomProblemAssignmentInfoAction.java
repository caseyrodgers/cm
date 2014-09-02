package hotmath.gwt.cm_rpc_assignments.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetCustomProblemAssignmentInfoAction implements Action<CmList<AssignmentModel>>{
	
	private String pid;

	public GetCustomProblemAssignmentInfoAction(){}
	
	public GetCustomProblemAssignmentInfoAction(String pid) {
		this.pid = pid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

}

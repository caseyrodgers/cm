package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetCorrelatedTopicsPrescriptionAction implements Action<CmList<PrescriptionSessionResponse>>{
	
	private ProblemDto pid;

	public GetCorrelatedTopicsPrescriptionAction(){}
	
	public GetCorrelatedTopicsPrescriptionAction(ProblemDto problem) {
		this.pid = problem;
	}

	public ProblemDto getPid() {
		return pid;
	}

	public void setPid(ProblemDto pid) {
		this.pid = pid;
	}

	@Override
	public String toString() {
		return "GetCorrelatedTopicsPrescriptionAction [pid=" + pid + "]";
	}
}

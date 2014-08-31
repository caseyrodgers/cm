package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetCorrelatedTopicsPrescriptionAction implements Action<CmList<PrescriptionSessionResponse>>{
	
	private String pid;

	public GetCorrelatedTopicsPrescriptionAction(){}
	
	public GetCorrelatedTopicsPrescriptionAction(String pid) {
		this.pid = pid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Override
	public String toString() {
		return "GetCorrelatedTopicsPrescriptionAction [pid=" + pid + "]";
	}
}

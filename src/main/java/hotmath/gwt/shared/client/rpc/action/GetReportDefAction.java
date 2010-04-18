package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StringHolder;

import java.util.List;

/**
 * Get the report def key for the specified <code>List<Integer></code> of student UIDs
 *
 * @author rfhall
 *
 */

public class GetReportDefAction implements Action<StringHolder> {

	private static final long serialVersionUID = -3417893900651052183L;

	private List<Integer> uids;

	public GetReportDefAction() {
	}

	public GetReportDefAction(List<Integer> uids) {
	    this.uids = uids;	
	}

	public List<Integer> getStudentUids() {
		return uids;
	}
}

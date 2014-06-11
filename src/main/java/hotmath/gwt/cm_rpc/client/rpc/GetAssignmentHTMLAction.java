package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentHTML;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetAssignmentHTMLAction implements Action<AssignmentHTML>{
    
	private static final long serialVersionUID = 7964158950220045553L;

	private int assignKey;
	private int numWorkLines;

    public GetAssignmentHTMLAction() {}
    
    public GetAssignmentHTMLAction(int assignKey) {
        this.assignKey = assignKey;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public int getNumWorkLines() {
		return numWorkLines;
	}

	public void setNumWorkLines(int numWorkLines) {
		this.numWorkLines = numWorkLines;
	}

	@Override
    public String toString() {
        return "GetAssignmentHTMLAction [assignKey=" + assignKey + ", numWorkLines=" + numWorkLines + "]";
    }
}

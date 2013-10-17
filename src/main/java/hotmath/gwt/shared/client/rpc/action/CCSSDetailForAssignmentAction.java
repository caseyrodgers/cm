package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CCSSDetail;

public class CCSSDetailForAssignmentAction implements Action<CmList<CCSSDetail>> {
    
	private static final long serialVersionUID = -180717987025614604L;

	private int assignmentKey ;
    
    public CCSSDetailForAssignmentAction() {
    }

    public CCSSDetailForAssignmentAction(int assignmentKey) {
        this.assignmentKey = assignmentKey;
    }

    public int getAssignmentKey() {
    	return assignmentKey;
    }
    
    @Override
    public String toString() {
        return "CCSSDetailAction [assignmentKey=" + assignmentKey + "]";
    }
}
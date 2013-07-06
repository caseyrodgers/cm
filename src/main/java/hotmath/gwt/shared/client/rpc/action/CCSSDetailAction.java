package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.shared.client.model.CCSSDetail;

public class CCSSDetailAction implements Action<CCSSDetail> {
    
	private static final long serialVersionUID = -1241219926825147218L;

	String standardName;
    
    public CCSSDetailAction() {
    }

    public CCSSDetailAction(String standardName) {
        this.standardName = standardName;
    }

    public String getStandardName() {
    	return standardName;
    }
    
    @Override
    public String toString() {
        return "CCSSDetailAction [standardName=" + standardName + "]";
    }
}
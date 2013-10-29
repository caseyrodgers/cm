package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CCSSDetail;

import java.util.List;

public class CCSSDetailAction implements Action<CmList<CCSSDetail>> {
    
	private static final long serialVersionUID = -1241219926825147218L;

	List<String> standardNames;
    
    public CCSSDetailAction() {
    }

    public CCSSDetailAction(List<String> standardNames) {
        this.standardNames = standardNames;
    }

    public List<String> getStandardNames() {
    	return standardNames;
    }
    
    @Override
    public String toString() {
        return "CCSSDetailAction [standardName=" + standardNames + "]";
    }
}
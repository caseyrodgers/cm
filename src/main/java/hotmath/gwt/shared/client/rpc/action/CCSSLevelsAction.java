package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CCSSGradeLevel;

public class CCSSLevelsAction implements Action<CmList<CCSSGradeLevel>> {
    
	private static final long serialVersionUID = 7662527568997648187L;

	public CCSSLevelsAction() {
    }

    @Override
    public String toString() {
        return "CCSSLevelsAction";
    }
}
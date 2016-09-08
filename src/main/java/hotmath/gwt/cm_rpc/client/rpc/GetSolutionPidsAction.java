package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetSolutionPidsAction implements Action<CmList<String>> {
    
	
	static public enum PidType {
		/** all dynamic solutions */
		DYNAMIC_SOLUTIONS,
		
		/** only out of date solutions */
		OUT_OF_DATE
	}
	private PidType type;
	
    public GetSolutionPidsAction(){}
    public GetSolutionPidsAction(PidType type) {
        this.type = type;
    }
    
    public PidType getType() {
		return type;
	}
    
    public void setType(PidType type) {
		this.type = type;
	}
}

package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetSolutionContextAction implements Action<CmList<SolutionContext>>{
    String pid;
    boolean lookupDetails;
    
    public GetSolutionContextAction() {}
    
    public GetSolutionContextAction(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
    
    
    public boolean isLookupDetails() {
		return lookupDetails;
	}
    
    public void setLookupDetails(boolean lookupDetails) {
		this.lookupDetails = lookupDetails;
	}
    

    @Override
    public String toString() {
        return "GetSolutionContextAction [pid=" + pid + "]";
    }
}

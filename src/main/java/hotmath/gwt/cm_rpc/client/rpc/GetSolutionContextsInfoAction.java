package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.SolutionContextsInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.ActionBase;


/** Return solution information for a given user and pid
 * 
 * @author casey
 *
 */
public class GetSolutionContextsInfoAction extends ActionBase implements Action<SolutionContextsInfo> {
    String pid;
    
    public GetSolutionContextsInfoAction() {}
    
    public GetSolutionContextsInfoAction(String pid) {
        this.pid = pid;
    }
    
    public String getPid() {
		return pid;
	}

}

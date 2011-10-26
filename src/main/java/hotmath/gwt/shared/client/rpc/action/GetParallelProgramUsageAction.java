package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.ParallelProgramUsageModel;

/** Return list Parallel Program Usage objects
 * 
 * @author bob
 *
 */
public class GetParallelProgramUsageAction implements Action<CmList<ParallelProgramUsageModel>>{
    
    Integer parallelProgId;

    public GetParallelProgramUsageAction(){}
    
    public Integer getParallelProgId() {
        return parallelProgId;
    }

    public void setParallelProgId(Integer id) {
        this.parallelProgId = id;
    }

    public GetParallelProgramUsageAction(Integer id) {
        this.parallelProgId = id;
    }

}

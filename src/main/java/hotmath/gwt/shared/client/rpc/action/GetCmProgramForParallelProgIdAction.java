package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.model.CmProgram;
import hotmath.gwt.cm_rpc.client.rpc.Action;

/** Return CmProgram for specified Parallel Program ID
 * 
 * @author bob
 *
 */
public class GetCmProgramForParallelProgIdAction implements Action<CmProgram>{
    
    Integer parallelProgId;

    public GetCmProgramForParallelProgIdAction(){}
    
    public GetCmProgramForParallelProgIdAction(Integer id) {
        this.parallelProgId = id;
    }

    public Integer getParallelProgId() {
        return parallelProgId;
    }

    public void setParallelProgId(Integer id) {
        this.parallelProgId = id;
    }

}

package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.ParallelProgramUsageModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;

/** Return Student for specified parallel program
 * 
 * @author bob
 *
 */
public class GetStudentForParallelProgramAction implements Action<StudentModelExt>{
    
    Integer parallelProgId;

    public GetStudentForParallelProgramAction(){}
    
    public GetStudentForParallelProgramAction(Integer id) {
        this.parallelProgId = id;
    }

    public Integer getParallelProgId() {
        return parallelProgId;
    }

    public void setParallelProgId(Integer id) {
        this.parallelProgId = id;
    }

}

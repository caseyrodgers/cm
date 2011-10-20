package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.ParallelProgramModel;

/** Return list of Parallel Programs and the student count for each
 * 
 * @author bob
 *
 */
public class GetParallelProgramsAction implements Action<CmList<ParallelProgramModel>>{
    
    Integer adminId;
    public GetParallelProgramsAction(){}
    
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public GetParallelProgramsAction(Integer adminId) {
        this.adminId = adminId;
    }

}

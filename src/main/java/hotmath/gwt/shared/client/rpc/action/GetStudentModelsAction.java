package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.StudentModelI;

public class GetStudentModelsAction implements Action<CmList<StudentModelI>> {
    
	CmList<Integer> uids;
    
    public GetStudentModelsAction() {}
    
    public GetStudentModelsAction(CmList<Integer> uids) {
        this.uids = uids;
    }

    public CmList<Integer> getUids() {
        return uids;
    }

    public void setUids(CmList<Integer> uids) {
        this.uids = uids;
    }
}

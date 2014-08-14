package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import java.util.List;

public class GetStudentModelsAction implements Action<CmList<StudentModelI>> {
    
	private static final long serialVersionUID = 2894777166679643008L;

	List<Integer> uids;
    
    public GetStudentModelsAction() {}
    
    public GetStudentModelsAction(List<Integer> uids) {
        this.uids = uids;
    }

    public List<Integer> getUids() {
        return uids;
    }

    public void setUids(List<Integer> uids) {
        this.uids = uids;
    }
}

package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.model.StudentModelI;

public class DeleteParallelProgramAction implements Action<RpcData>{
    
    Integer adminId;
    Integer parallelProgId;
    
    public DeleteParallelProgramAction(){}
    
    public DeleteParallelProgramAction(Integer adminId, Integer parallelProgId) {
        this.adminId = adminId;
        this.parallelProgId = parallelProgId;
    }
    
    public Integer getAdminId() {
        return adminId;
    }
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
    
    public Integer getParallelProgId() {
		return parallelProgId;
	}

	public void setParallelProgId(Integer parallelProgId) {
		this.parallelProgId = parallelProgId;
	}

	@Override
    public String toString() {
        return "adminId: " + adminId + ", parallelProgId: " + parallelProgId;	
    }
}


package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class SaveParallelProgramAction implements Action<RpcData>{
    
    Integer adminId;
    StudentModelI student;
    Integer parallelProgId;
    
    public SaveParallelProgramAction(){}
    
    public SaveParallelProgramAction(Integer adminId, StudentModelI student, Integer parallelProgId) {
        this.adminId = adminId;
        this.student = student;
        this.parallelProgId = parallelProgId;
    }
    
    public Integer getAdminId() {
        return adminId;
    }
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
    public StudentModelI getStudent() {
        return student;
    }
    public void setStudent(StudentModelI student) {
        this.student = student;
    }

	public Integer getParallelProgId() {
		return parallelProgId;
	}

	public void setParallelProgId(Integer parallelProgId) {
		this.parallelProgId = parallelProgId;
	}

	@Override
    public String toString() {
        return "adminId: " + adminId + ", parallelProgId: " + parallelProgId + ", student: " + ((student != null)?"Not NULL":"NULL");	
    }
}


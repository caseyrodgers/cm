package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.util.RpcData;

public class SaveAutoRegistrationAction implements Action<RpcData>{
    
    Integer adminId;
    StudentModelI student;
    
    public SaveAutoRegistrationAction(){}
    
    public SaveAutoRegistrationAction(Integer adminId, StudentModelI student) {
        this.adminId = adminId;
        this.student = student;
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
}

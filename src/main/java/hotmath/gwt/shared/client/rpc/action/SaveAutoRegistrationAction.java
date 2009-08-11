package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

public class SaveAutoRegistrationAction implements Action<RpcData>{
    
    Integer adminId;
    StudentModel student;
    
    public SaveAutoRegistrationAction(){}
    
    public SaveAutoRegistrationAction(Integer adminId, StudentModel student) {
        this.adminId = adminId;
        this.student = student;
    }
    
    public Integer getAdminId() {
        return adminId;
    }
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
    public StudentModel getStudent() {
        return student;
    }
    public void setStudent(StudentModel student) {
        this.student = student;
    }
}

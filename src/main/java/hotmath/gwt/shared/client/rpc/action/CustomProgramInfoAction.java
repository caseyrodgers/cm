package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.shared.client.model.CustomProgramInfoModel;

public class CustomProgramInfoAction implements Action<CustomProgramInfoModel>{
    CustomProgramModel program;
    Integer adminId;
    

    public CustomProgramInfoAction(){}

    public CustomProgramInfoAction(Integer adminId, CustomProgramModel program) {
        this.adminId = adminId;
        this.program = program;
    }
    
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    
    public CustomProgramModel getProgram() {
        return program;
    }

    public void setProgram(CustomProgramModel program) {
        this.program = program;
    }

    @Override
    public String toString() {
        return "CustomProgramInfoAction [adminId=" + adminId + ", program=" + program + "]";
    }
}    

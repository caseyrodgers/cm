package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;

public class CustomProgramDefinitionAction implements Action<CmList<CustomProgramModel>>{
    
    Integer adminId;
    ActionType action;
    Integer programId;

    public CustomProgramDefinitionAction() {
        /** empty */
    }
    public CustomProgramDefinitionAction(ActionType actionType, Integer adminId) {
        this.action = actionType;
        this.adminId = adminId;
    }
    
    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }
    
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getProgramId() {
        return programId;
    }
    public void setProgramId(Integer programId) {
        this.programId = programId;
    }    

    static public enum ActionType{GET, DELETE};
}

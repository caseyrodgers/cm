package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActionBase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;

public class CustomProgramDefinitionAction extends ActionBase implements Action<CmList<CustomProgramModel>>{
    
    Integer adminId;
    ActionType action;
    Integer programId;

    public CustomProgramDefinitionAction() {
    	getClientInfo().setUserType(UserType.ADMIN);
    }

    public CustomProgramDefinitionAction(ActionType actionType, Integer adminId) {
        this.action = actionType;
        this.adminId = adminId;
        getClientInfo().setUserId((adminId != null)?adminId:0);
    	getClientInfo().setUserType(UserType.ADMIN);
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

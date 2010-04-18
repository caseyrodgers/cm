package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.AutoUserAdvanced;

public class AutoAdvanceUserAction implements Action<AutoUserAdvanced>{

    Integer userId;
    
    public AutoAdvanceUserAction() {}
    
    public AutoAdvanceUserAction(Integer userId){
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AutoAdvanceUserAction [userId=" + userId + "]";
    }
}

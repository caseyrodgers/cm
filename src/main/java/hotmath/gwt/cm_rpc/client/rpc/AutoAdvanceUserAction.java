package hotmath.gwt.cm_rpc.client.rpc;


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

package hotmath.gwt.hm_mobile.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;

public class HmMobileLoginAction implements Action<HmMobileLoginInfo>{
    
    private String user;
    private String password;

    public HmMobileLoginAction(){}
    
    public HmMobileLoginAction(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "HmMobileLoginAction [user=" + user + ", password=" + password + "]";
    }
}

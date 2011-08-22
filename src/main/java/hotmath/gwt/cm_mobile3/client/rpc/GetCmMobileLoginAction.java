package hotmath.gwt.cm_mobile3.client.rpc;

import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_rpc.client.rpc.Action;

public class GetCmMobileLoginAction implements Action<CmMobileUser> {
    
    String name,password;
    
    public GetCmMobileLoginAction(){}
    
    public GetCmMobileLoginAction(String user, String pass) {
        this.name = user;
        this.password = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "GetCmMobileLoginAction [name=" + name + ", password=" + password + "]";
    }
}

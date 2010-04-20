package hotmath.gwt.cm_mobile.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;

public class GetCmMobileLoginAction implements Action<CmMobileUser>{
    String name;
    String password;
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GetCmMobileLoginAction() {
    }
}

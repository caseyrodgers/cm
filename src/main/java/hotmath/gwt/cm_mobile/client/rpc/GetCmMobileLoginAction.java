package hotmath.gwt.cm_mobile.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;

public class GetCmMobileLoginAction implements Action<CmMobileUser>{
    
    String name;
    String password;
    int uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public GetCmMobileLoginAction() {
    }    
    
    public GetCmMobileLoginAction(String name, String password) {
        this.name = name;
        this.password = password;
    }
    
    public GetCmMobileLoginAction(int uid) {
        this.uid = uid;
    }
    
    
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

    @Override
    public String toString() {
        return "GetCmMobileLoginAction [name=" + name + ", password=" + password + ", uid=" + uid + "]";
    }

}

package hotmath.gwt.cm_rpc.client.rpc.cm2;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetCm2MobileLoginAction implements Action<Cm2MobileUser> {
    
    String name,password;
    int uid;
    
    public GetCm2MobileLoginAction(){}
    
    public GetCm2MobileLoginAction(String user, String pass) {
        this.name = user;
        this.password = pass;
    }
    
    public GetCm2MobileLoginAction(int uid) {
        this.uid = uid;
    }


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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
        return "GetCmMobileLoginAction [name=" + name + ", password=" + password + ", uid=" + uid + "]";
    }
}

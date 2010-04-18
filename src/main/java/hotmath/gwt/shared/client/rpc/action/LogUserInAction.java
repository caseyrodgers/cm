package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

/**
 *  Log user with password into Cm and return login key
 *  
 * @author casey
 *
 */
public class LogUserInAction implements Action<RpcData> {
    
    String password;
    String userName;
    
    
    public LogUserInAction() {}
    
    public LogUserInAction(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

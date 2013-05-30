package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

/**
 *  Log user with password into Cm and return login key
 *  
 *  TODO: why is this duplicated in LoginAction?
 *  
 * @author casey
 *
 */
public class LogUserInAction implements Action<RpcData> {
    
    String password;
    String userName;
    String browserInfo;
    
    
    public LogUserInAction() {}
    
    public LogUserInAction(String userName, String password, String browserInfo) {
        this.userName = userName;
        this.password = password;
        this.browserInfo = browserInfo;
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

    public String getBrowserInfo() {
        return browserInfo;
    }

    public void setBrowserInfo(String browserInfo) {
        this.browserInfo = browserInfo;
    }

    @Override
    public String toString() {
        return "LogUserInAction [password=" + password + ", userName=" + userName + ", browserInfo=" + browserInfo
                + "]";
    }
}

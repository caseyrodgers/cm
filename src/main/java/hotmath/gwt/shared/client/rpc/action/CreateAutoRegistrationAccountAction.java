package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActionBase;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;


/** Create a single user account using the Auto Registration system
 * 
 * @author casey
 *
 */
public class CreateAutoRegistrationAccountAction extends ActionBase implements Action<RpcData> {
    Integer userId;
    String user;
    String password;

    public CreateAutoRegistrationAccountAction() {
        getClientInfo().setUserType(UserType.STUDENT);
    }
    
    public CreateAutoRegistrationAccountAction(Integer userId, String user, String password) {
        this.userId = userId;
        this.user = user;
        this.password = password;
        
        getClientInfo().setUserId(userId);
        getClientInfo().setUserType(UserType.STUDENT);
    }


    public Integer getUserId() {
        return userId;
    }


    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public String getUser() {
        return user;
    }


    public void setUser(String user) {
        this.user = user;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }
    
}

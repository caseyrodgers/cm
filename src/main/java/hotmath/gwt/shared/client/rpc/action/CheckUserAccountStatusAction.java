package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.ClientInfo.UserType;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ActionBase;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;


/** Provide basic checking of user accounts from client
 * 
 * @author casey
 *
 */
public class CheckUserAccountStatusAction extends ActionBase implements Action<RpcData>{
    
    String password;
    
    public CheckUserAccountStatusAction(){
    	setClientInfo();
    }
    
    public CheckUserAccountStatusAction(String password) {
        this.password = password;
    	setClientInfo();
    }
    
    
    public String getPassword() {
        return password;
    }
    
    
    public void setPassword(String password) {
        this.password = password;
    }

    private void setClientInfo() {
    	// currently, only used for self-registration 
    	getClientInfo().setUserType(UserType.STUDENT);
    }
}

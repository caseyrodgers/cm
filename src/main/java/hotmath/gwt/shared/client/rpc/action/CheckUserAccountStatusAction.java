package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;


/** Provide basic checking of user accounts from client
 * 
 * @author casey
 *
 */
public class CheckUserAccountStatusAction implements Action<RpcData>{
    
    String password;
    
    public CheckUserAccountStatusAction(){}
    
    public CheckUserAccountStatusAction(String password) {
        this.password = password;
    }
    
    
    public String getPassword() {
        return password;
    }
    
    
    public void setPassword(String password) {
        this.password = password;
    }

}

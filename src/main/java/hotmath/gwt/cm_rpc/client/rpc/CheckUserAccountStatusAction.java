package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;


/** Provide basic checking of user accounts from client
 * 
 * @author casey
 *
 */
public class CheckUserAccountStatusAction implements Action<RpcData>{
    
    String password;
    
    public CheckUserAccountStatusAction(){
    }
    
    public CheckUserAccountStatusAction(String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
    	return "CheckUserAccountStatusAction: [ password= " + password + " ]";
    }

}

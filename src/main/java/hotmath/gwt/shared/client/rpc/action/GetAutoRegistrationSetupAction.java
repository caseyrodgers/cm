package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;

/** Action to Read the current Auto Registration Setup for
 *  the named user.
 *  
 * @author casey
 *
 */
public class GetAutoRegistrationSetupAction implements Action<AutoRegistrationSetup> {
    Integer adminId;


    public GetAutoRegistrationSetupAction() {
    }
    
    
    public GetAutoRegistrationSetupAction(Integer adminId) {
        this.adminId = adminId;
    }
    
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}

package hotmath.gwt.cm_mobile_shared.client.event;

import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;

import com.google.gwt.event.shared.EventHandler;


public interface UserLoginHandler extends EventHandler {
    void userLogin(CmMobileUser user);
}

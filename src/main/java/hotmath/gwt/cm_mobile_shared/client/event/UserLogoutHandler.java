package hotmath.gwt.cm_mobile_shared.client.event;

import com.google.gwt.event.shared.EventHandler;

/** Fired when user has logged out of mobile app
 * 
 * @author casey
 *
 */
public interface UserLogoutHandler extends EventHandler {
    void userLogout();
}

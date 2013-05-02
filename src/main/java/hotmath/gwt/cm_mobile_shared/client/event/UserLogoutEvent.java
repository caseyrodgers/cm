package hotmath.gwt.cm_mobile_shared.client.event;

import com.google.gwt.event.shared.GwtEvent;

/** Fired when user logs out of mobile system
 * 
 * @author casey
 *
 */
public class UserLogoutEvent extends GwtEvent<UserLogoutHandler>{
    
    public static Type<UserLogoutHandler> TYPE = new Type<UserLogoutHandler>();
    
    @Override
    public Type<UserLogoutHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UserLogoutHandler handler) {
        handler.userLogout();
    }
}

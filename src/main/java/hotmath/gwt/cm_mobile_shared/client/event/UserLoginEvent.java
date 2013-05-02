package hotmath.gwt.cm_mobile_shared.client.event;

import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;

import com.google.gwt.event.shared.GwtEvent;

public class UserLoginEvent extends GwtEvent<UserLoginHandler>{
    
    public static Type<UserLoginHandler> TYPE = new Type<UserLoginHandler>();
    private CmMobileUser user;
    
    public UserLoginEvent(CmMobileUser user) {
        this.user = user;
    }
    
    @Override
    public Type<UserLoginHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UserLoginHandler handler) {
        handler.userLogin(user);
    }
}

package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;

import com.google.web.bindery.event.shared.Event;

public class HmLoginEvent extends Event<HmLoginHandler> {

    private HmMobileLoginInfo loginInfo;

    public HmLoginEvent(HmMobileLoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }
    @Override
    protected void dispatch(HmLoginHandler handler) {
        handler.userLogin(loginInfo);
    }
    
    public static final com.google.web.bindery.event.shared.Event.Type<HmLoginHandler> TYPE = new Type<HmLoginHandler>();

    @Override
    public com.google.web.bindery.event.shared.Event.Type<HmLoginHandler> getAssociatedType() {
        return TYPE;
    }

}

package hotmath.gwt.hm_mobile.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class HmLogoutEvent extends GwtEvent<HmLogoutHandler> {

    public static final com.google.gwt.event.shared.GwtEvent.Type<HmLogoutHandler> TYPE = new Type<HmLogoutHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<HmLogoutHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(HmLogoutHandler handler) {
        handler.userLogOut();
    }

}

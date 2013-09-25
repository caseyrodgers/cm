package hotmath.gwt.cm_core.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CmLogoutEvent extends GwtEvent<CmLogoutHandler> {

    public static final com.google.gwt.event.shared.GwtEvent.Type<CmLogoutHandler> TYPE = new Type<CmLogoutHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CmLogoutHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CmLogoutHandler handler) {
        handler.userLogOut();
    }

}

package hotmath.gwt.hm_mobile.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowLoginViewEvent extends GwtEvent<ShowLoginViewHandler> {

    @Override
    protected void dispatch(ShowLoginViewHandler handler) {
        handler.showLoginView();
    }


    public static final com.google.gwt.event.shared.GwtEvent.Type<ShowLoginViewHandler> TYPE = new Type<ShowLoginViewHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowLoginViewHandler> getAssociatedType() {
        return TYPE;
    }
}

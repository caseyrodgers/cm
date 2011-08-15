package hotmath.gwt.cm_mobile3.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowLoginViewEvent extends GwtEvent<ShowLoginViewHandler>{

    public static Type<ShowLoginViewHandler> TYPE = new Type<ShowLoginViewHandler>();

    public ShowLoginViewEvent() {
    }
    
    @Override
    public Type<ShowLoginViewHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowLoginViewHandler handler) {
        handler.showLoginView();
    }

}

package hotmath.gwt.cm_core.client.event;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.event.shared.GwtEvent;

public class RppHasBeenViewedEvent extends GwtEvent<RppHasBeenViewedEventHandler> {

    public static final Type<RppHasBeenViewedEventHandler> TYPE = new Type<RppHasBeenViewedEventHandler>();
    private RppHasBeenViewedEvent item;
    
    public RppHasBeenViewedEvent(InmhItemData item) {
        this.item = this;
    }

    @Override
    public Type<RppHasBeenViewedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RppHasBeenViewedEventHandler handler) {
        handler.rppHasBeenViewed(item);
    }

}

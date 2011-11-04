package hotmath.gwt.cm_mobile_shared.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowFlashRequiredEvent extends GwtEvent<ShowFlashRequiredEventHandler>{

    public static Type<ShowFlashRequiredEventHandler> TYPE = new Type<ShowFlashRequiredEventHandler>();

    public ShowFlashRequiredEvent() {
    }
    
    @Override
    public Type<ShowFlashRequiredEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowFlashRequiredEventHandler handler) {
        handler.showFlashRequiredDialog();
    }
}

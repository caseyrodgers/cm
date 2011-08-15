package hotmath.gwt.cm_mobile3.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowWorkViewEvent extends GwtEvent<ShowWorkViewHandler>{

    public static Type<ShowWorkViewHandler> TYPE = new Type<ShowWorkViewHandler>();

    public ShowWorkViewEvent() {
    }
    
    @Override
    public Type<ShowWorkViewHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowWorkViewHandler handler) {
        handler.showWorkView();
    }

}

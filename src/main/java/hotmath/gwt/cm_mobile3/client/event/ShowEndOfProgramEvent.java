package hotmath.gwt.cm_mobile3.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowEndOfProgramEvent extends GwtEvent<ShowEndOfProgramEventHandler>{

    public static Type<ShowEndOfProgramEventHandler> TYPE = new Type<ShowEndOfProgramEventHandler>();

    public ShowEndOfProgramEvent() {
    }
    
    @Override
    public Type<ShowEndOfProgramEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowEndOfProgramEventHandler handler) {
        handler.showView();
    }
}

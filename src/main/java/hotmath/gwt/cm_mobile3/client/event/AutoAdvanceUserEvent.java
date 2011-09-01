package hotmath.gwt.cm_mobile3.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AutoAdvanceUserEvent extends GwtEvent<AutoAdvanceUserEventHandler>{

    public static Type<AutoAdvanceUserEventHandler> TYPE = new Type<AutoAdvanceUserEventHandler>();

    int userId;
    public AutoAdvanceUserEvent(int userId) {
        this.userId = userId;
    }
    
    @Override
    public Type<AutoAdvanceUserEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AutoAdvanceUserEventHandler handler) {
        handler.autoAdvanceUser(userId);
    }
}

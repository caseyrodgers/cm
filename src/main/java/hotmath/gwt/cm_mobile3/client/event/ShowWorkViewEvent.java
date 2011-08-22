package hotmath.gwt.cm_mobile3.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowWorkViewEvent extends GwtEvent<ShowWorkViewHandler>{

    public static Type<ShowWorkViewHandler> TYPE = new Type<ShowWorkViewHandler>();

    String pid;
    public ShowWorkViewEvent() {
    }
    
    public ShowWorkViewEvent(String pid) {
        this.pid = pid;
    }
    
    @Override
    public Type<ShowWorkViewHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowWorkViewHandler handler) {
        handler.showWorkView(pid);
    }

}

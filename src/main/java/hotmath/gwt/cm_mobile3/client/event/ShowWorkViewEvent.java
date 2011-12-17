package hotmath.gwt.cm_mobile3.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowWorkViewEvent extends GwtEvent<ShowWorkViewHandler>{

    public static Type<ShowWorkViewHandler> TYPE = new Type<ShowWorkViewHandler>();

    String pid;
    String title;
    
    public ShowWorkViewEvent() {
    }
    
    public ShowWorkViewEvent(String pid, String title) {
        this.pid = pid;
        this.title = title;
    }
    
    @Override
    public Type<ShowWorkViewHandler> getAssociatedType() {
        return TYPE;
    }
    

    @Override
    protected void dispatch(ShowWorkViewHandler handler) {
        handler.showWorkView(pid, title);
    }

}

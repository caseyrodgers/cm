package hotmath.gwt.cm_mobile3.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowWelcomeViewEvent extends GwtEvent<ShowWelcomeViewHandler>{

    public static Type<ShowWelcomeViewHandler> TYPE = new Type<ShowWelcomeViewHandler>();

    public ShowWelcomeViewEvent() {
    }
    
    @Override
    public Type<ShowWelcomeViewHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowWelcomeViewHandler handler) {
        handler.showWelcomeView();
    }

}

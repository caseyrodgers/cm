package hotmath.gwt.cm_mobile3.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowQuizViewEvent extends GwtEvent<ShowQuizViewHandler>{

    public static Type<ShowQuizViewHandler> TYPE = new Type<ShowQuizViewHandler>();

    public ShowQuizViewEvent() {
    }
    
    @Override
    public Type<ShowQuizViewHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowQuizViewHandler handler) {
        handler.showQuizView();
    }

}

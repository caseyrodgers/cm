package hotmath.gwt.cm_rpc.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class TutorContainerActivatedEvent extends GwtEvent<TutorContainerActivatedEventHandler> {

    public static Type<TutorContainerActivatedEventHandler> TYPE = new Type<TutorContainerActivatedEventHandler>();

    @Override
    public Type<TutorContainerActivatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TutorContainerActivatedEventHandler handler) {
        handler.tutorContainerActivated(this);
    }

}

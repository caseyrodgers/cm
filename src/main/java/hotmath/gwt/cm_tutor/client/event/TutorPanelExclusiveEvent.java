package hotmath.gwt.cm_tutor.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class TutorPanelExclusiveEvent extends GwtEvent<TutorPanelExclusiveHandler> {

    public static Type<TutorPanelExclusiveHandler> TYPE = new Type<TutorPanelExclusiveHandler>();
    
    @Override
    public Type<TutorPanelExclusiveHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TutorPanelExclusiveHandler handler) {
        handler.tutorNeedsExclusiveAccess();
    }


}

package hotmath.gwt.cm_mobile3.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class MoveToNextSegmentEvent extends GwtEvent<MoveToNextSegmentEventHandler>{

    public static Type<MoveToNextSegmentEventHandler> TYPE = new Type<MoveToNextSegmentEventHandler>();
    
    public MoveToNextSegmentEvent() {
    }

    @Override
    public Type<MoveToNextSegmentEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(MoveToNextSegmentEventHandler handler) {
        handler.loadNextSegment();
    }

}

package hotmath.gwt.cm_core.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CmQuizModeActivatedEvent extends GwtEvent<CmQuizModeActivatedEventHandler>{

    private boolean yesNo;
    public CmQuizModeActivatedEvent(boolean yesNo) {
        this.yesNo = yesNo;
    }
    public static Type<CmQuizModeActivatedEventHandler> TYPE = new Type<CmQuizModeActivatedEventHandler>();
    @Override
    public Type<CmQuizModeActivatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CmQuizModeActivatedEventHandler handler) {
        handler.quizModeActivated(yesNo);
    }

}

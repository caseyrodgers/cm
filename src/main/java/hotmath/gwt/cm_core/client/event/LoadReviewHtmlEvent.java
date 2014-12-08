package hotmath.gwt.cm_core.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class LoadReviewHtmlEvent extends GwtEvent<LoadReviewHtmlHandler> {
    
    private String file;

    private int uniqueInstanceKey;

    public static Type<LoadReviewHtmlHandler> TYPE = new Type<LoadReviewHtmlHandler>();
    
    public LoadReviewHtmlEvent(String file, int uniqueInstanceKey) {
        this.file = file;
        this.uniqueInstanceKey = uniqueInstanceKey;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<LoadReviewHtmlHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LoadReviewHtmlHandler handler) {
        handler.loadLesson(file, uniqueInstanceKey);
    }

}

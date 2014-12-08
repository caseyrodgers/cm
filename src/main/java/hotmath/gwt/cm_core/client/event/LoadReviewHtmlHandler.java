package hotmath.gwt.cm_core.client.event;

import com.google.gwt.event.shared.EventHandler;



public interface LoadReviewHtmlHandler extends EventHandler {
    void loadLesson(String file, int uniqueInstanceKey);
}

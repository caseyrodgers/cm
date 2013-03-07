package hotmath.gwt.cm.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface AssignmentsUpdatedHandler extends EventHandler {
    void assignmentsUpdated(int activeAssignments, int unreadFeedback);
}

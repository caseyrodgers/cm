package hotmath.gwt.shared.client.event;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;

import com.google.gwt.event.shared.EventHandler;

public interface AssignmentsUpdatedHandler extends EventHandler {
    void assignmentsUpdated(AssignmentUserInfo info);
}

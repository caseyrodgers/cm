package hotmath.gwt.shared.client.event;

import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentMetaInfo;

import com.google.gwt.event.shared.EventHandler;

public interface AssignmentsUpdatedHandler extends EventHandler {
    void assignmentsUpdated(AssignmentMetaInfo info);
}

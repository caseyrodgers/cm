package hotmath.gwt.cm_tools.client.ui.assignment.event;

import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentProblem;

import com.google.gwt.event.shared.EventHandler;

public interface AssignmentProblemLoadedHandler extends EventHandler {
    void assignmentProblemLoaded(AssignmentProblem assProb);
}

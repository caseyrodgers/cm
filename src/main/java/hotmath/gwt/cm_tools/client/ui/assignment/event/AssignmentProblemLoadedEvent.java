package hotmath.gwt.cm_tools.client.ui.assignment.event;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;

import com.google.gwt.event.shared.GwtEvent;

/** Inform UI that a new AssignmentProblem has been loaded
 * 
 * @author casey
 *
 */
public class AssignmentProblemLoadedEvent extends GwtEvent<AssignmentProblemLoadedHandler> {

    AssignmentProblem assignmentProblem;
    public AssignmentProblem getAssignmentProblem() {
        return assignmentProblem;
    }

    public void setAssignmentProblem(AssignmentProblem assignmentProblem) {
        this.assignmentProblem = assignmentProblem;
    }

    public AssignmentProblemLoadedEvent(AssignmentProblem assProb) {
        this.assignmentProblem = assProb;
    }
    public static Type<AssignmentProblemLoadedHandler> TYPE = new Type<AssignmentProblemLoadedHandler>();
    
    @Override
    public Type<AssignmentProblemLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AssignmentProblemLoadedHandler handler) {
        handler.assignmentProblemLoaded(assignmentProblem);
    }

}

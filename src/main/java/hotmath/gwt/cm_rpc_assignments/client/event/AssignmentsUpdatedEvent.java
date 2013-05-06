package hotmath.gwt.cm_rpc_assignments.client.event;


import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;

import com.google.gwt.event.shared.GwtEvent;

public class AssignmentsUpdatedEvent extends GwtEvent<AssignmentsUpdatedHandler> {

    public static Type<AssignmentsUpdatedHandler> TYPE = new Type<AssignmentsUpdatedHandler>();
    private AssignmentUserInfo assignmentInfo;
    
    
    public AssignmentsUpdatedEvent(AssignmentUserInfo assignmentInfo) {
        this.assignmentInfo = assignmentInfo; 
    }
    
    

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AssignmentsUpdatedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AssignmentsUpdatedHandler handler) {
        handler.assignmentsUpdated(assignmentInfo);
    }

}

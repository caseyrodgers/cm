package hotmath.gwt.shared.client.event;


import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentMetaInfo;

import com.google.gwt.event.shared.GwtEvent;

public class AssignmentsUpdatedEvent extends GwtEvent<AssignmentsUpdatedHandler> {

    public static Type<AssignmentsUpdatedHandler> TYPE = new Type<AssignmentsUpdatedHandler>();
    private AssignmentMetaInfo assignmentInfo;
    
    
    public AssignmentsUpdatedEvent(AssignmentMetaInfo assignmentInfo) {
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

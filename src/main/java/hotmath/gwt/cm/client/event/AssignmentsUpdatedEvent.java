package hotmath.gwt.cm.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AssignmentsUpdatedEvent extends GwtEvent<AssignmentsUpdatedHandler> {

    public static Type<AssignmentsUpdatedHandler> TYPE = new Type<AssignmentsUpdatedHandler>();
    private int activeAssignments;
    private int unreadFeedback;
    
    public AssignmentsUpdatedEvent(int activeAssignments, int unreadFeedback) {
        this.activeAssignments = activeAssignments;
        this.unreadFeedback = unreadFeedback;
    }
    
    

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AssignmentsUpdatedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AssignmentsUpdatedHandler handler) {
        handler.assignmentsUpdated(activeAssignments, unreadFeedback);
    }



    public int getActiveAssignments() {
        return activeAssignments;
    }



    public void setActiveAssignments(int activeAssignments) {
        this.activeAssignments = activeAssignments;
    }



    public int getUnreadFeedback() {
        return unreadFeedback;
    }



    public void setUnreadFeedback(int unreadFeedback) {
        this.unreadFeedback = unreadFeedback;
    }

}

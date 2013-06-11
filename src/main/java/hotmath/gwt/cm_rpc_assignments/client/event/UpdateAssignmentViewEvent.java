package hotmath.gwt.cm_rpc_assignments.client.event;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.GwtEvent;

public class UpdateAssignmentViewEvent extends GwtEvent<UpdateAssignmentViewHandler> {

    public static Type<UpdateAssignmentViewHandler> TYPE = new Type<UpdateAssignmentViewHandler>();
    
    /** The underlying data has been updated and any UI view 
     *  needs to be refreshed from scratch.
     *  
     */
    public UpdateAssignmentViewEvent() {
        Log.debug("Fired UpdateAssignmentViewEvent: " + System.currentTimeMillis());
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UpdateAssignmentViewHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UpdateAssignmentViewHandler handler) {
        handler.updateView();
    }

}

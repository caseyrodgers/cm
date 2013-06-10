package hotmath.gwt.cm_rpc_assignments.client.event;

import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;

import com.google.gwt.event.shared.GwtEvent;

public class UpdateAssignmentViewEvent extends GwtEvent<UpdateAssignmentViewHandler> {

    public static Type<UpdateAssignmentViewHandler> TYPE = new Type<UpdateAssignmentViewHandler>();
    
    /** The underlying data has been updated and any UI view 
     *  needs to be refreshed from scratch.
     *  
     */
    public UpdateAssignmentViewEvent() {
        MessageBox.showMessage("Fired UpdateAssignmentViewEvent: " + System.currentTimeMillis());
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

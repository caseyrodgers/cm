package hotmath.gwt.cm_tools.client.ui.assignment.event;

import hotmath.gwt.cm_tools.client.ui.assignment.action.StudentAssignmentViewerActivatedHandler;

import com.google.gwt.event.shared.GwtEvent;

/** Fired when the StudentAssignmentViewer gui is activated.
 * 
 * This allows other GUI components to 'close', such as tutors/whiteboards
 * that might become corrupted due to duplicated views.
 * 
 * @author casey
 *
 */
public class StudentAssignmentViewerActivatedAction extends GwtEvent<StudentAssignmentViewerActivatedHandler> {

    public static Type<StudentAssignmentViewerActivatedHandler> TYPE = new Type<StudentAssignmentViewerActivatedHandler>();
    
    @Override
    public Type<StudentAssignmentViewerActivatedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StudentAssignmentViewerActivatedHandler handler) {
        handler.assignmentGuiActivated();
    }

}

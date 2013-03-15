package hotmath.gwt.shared.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class LoadAssignmentProblemEvent extends GwtEvent<LoadAssignmentProblemHandler> {
    
    private int assignKey;
    private String pid;
    public LoadAssignmentProblemEvent(int assignKey, String pid) {
        this.assignKey = assignKey;
        this.pid = pid;
    }

    @Override
    protected void dispatch(LoadAssignmentProblemHandler handler) {
        handler.loadAssignmentProblem(assignKey, pid);
    }
    
    public static Type<LoadAssignmentProblemHandler> TYPE = new Type<LoadAssignmentProblemHandler>();
    
    @Override
    public Type<LoadAssignmentProblemHandler> getAssociatedType() {
       return TYPE;
    }
}

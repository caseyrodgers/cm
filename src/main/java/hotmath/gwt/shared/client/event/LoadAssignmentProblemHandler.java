package hotmath.gwt.shared.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface LoadAssignmentProblemHandler extends EventHandler{
    void loadAssignmentProblem(int assignKey, String pid);
}

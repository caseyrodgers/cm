package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;

import com.google.gwt.user.client.ui.IsWidget;

public interface AssignmentView extends IPage, IsWidget {
    void setPresenter(Presenter listener, CallbackOnComplete callbackOnComplete);
    public interface Presenter {
        void turnInAssignment(AssignmentView view);
        void showProblem(StudentProblemDto problem);
        void gotoAssignmentList();
        void refreshAssignment(AssignmentView view);
        void loadAssignment(AssignmentView view);
    }
    void loadAssignment(StudentAssignment assignment);
}

package hotmath.gwt.cm_mobile_shared.client.view;

import java.util.List;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;

import com.google.gwt.user.client.ui.IsWidget;

public interface AssignmentListView extends IPage, IsWidget {
    void setPresenter(Presenter listener);
    public interface Presenter {
        void readDataFromServer(AssignmentListView view, boolean force);
        void showAssignment(StudentAssignmentInfo studentAssignmentInfo);
    }
    void displayAssigmments(List<StudentAssignmentInfo> assignments);     
}

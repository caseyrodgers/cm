package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;

import com.google.gwt.user.client.ui.IsWidget;

public interface AssignmentProblemView extends IPage, IsWidget {
    void setPresenter(Presenter listener);
    public interface Presenter {
        void gotoAssignment();

        String getProblemTitle();
        void fetchProblem(AssignmentProblemView view);

        void markSolutionAsComplete();

        void newProblem();

        void processTutorWidgetComplete(String inputValue, boolean correct);

        void showWhiteboard(String title);

        boolean isAssignmentGraded();

        InmhItemData getItemData();

        void showWorkHasBeenSubmitted();
    }
    void loadProblem(AssignmentProblem problem);
}

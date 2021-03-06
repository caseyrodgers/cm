package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;

import com.google.gwt.user.client.ui.IsWidget;

public interface AssignmentProblemView extends IPage, IsWidget {
    void setPresenter(Presenter presenter, boolean showWork, CallbackOnComplete callbackOnComplete);
    public interface Presenter {
        void gotoAssignment();

        String getProblemTitle();
        void fetchProblem(AssignmentProblemView assignmentProblemView, boolean shouldShowWhiteboard, CallbackOnComplete callback);

        void markSolutionAsComplete();

        void newProblem();

        void processTutorWidgetComplete(String inputValue, boolean correct);

        void showWhiteboard(ShowWorkPanel2 showWorkPanel);

        boolean isAssignmentGraded();

        InmhItemData getItemData();

        void showWorkHasBeenSubmitted();

        Action<? extends Response> getWhiteboardSaveAction(String pid, CommandType commandType, String data);

        void showLesson(LessonModel lesson);

    }
    
    void loadProblem(AssignmentProblem problem);
    void showWhiteboard();
}

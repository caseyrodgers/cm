package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.HasWhiteboard;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;

public interface PrescriptionLessonResourceTutorView extends IPage,HasWhiteboard {
    void loadSolution(SolutionInfo solution);
    void setPresenter(Presenter p);
    ProblemNumber getLoadedProblem();
    void setHeaderTitle(String title);
    
    
    static public interface Presenter {
        void setupView(PrescriptionLessonResourceTutorView view);
        void markSolutionAsComplete();
        InmhItemData getItemData();
        void showWhiteboard(ShowWorkPanel2 _showWork);
        Action<? extends Response> getWhiteboardSaveAction(String pid, CommandType commandType, String data);
    }
}

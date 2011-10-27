package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.HasWhiteboard;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

public interface PrescriptionLessonResourceTutorView extends IPage,HasWhiteboard {
    void loadSolution(SolutionResponse solution);
    void setPresenter(Presenter p);
    ProblemNumber getLoadedProblem();
    void setTitle(String title);
    
    
    static public interface Presenter {
        void setupView(PrescriptionLessonResourceTutorView view);
        void showWhiteboard();
        void markSolutionAsComplete();
        InmhItemData getItemData();
    }
}

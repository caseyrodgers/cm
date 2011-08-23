package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

public interface PrescriptionLessonResourceTutorView extends IPage {
    void loadSolution(SolutionResponse solution);
    void setPresenter(Presenter p);
    ProblemNumber getLoadedProblem();
    
    
    static public interface Presenter {
        void setupView(PrescriptionLessonResourceTutorView view);
        void showWhiteboard();
        void markSolutionAsComplete();
    }
}

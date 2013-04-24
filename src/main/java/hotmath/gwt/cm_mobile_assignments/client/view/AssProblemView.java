package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;

public interface AssProblemView extends BaseView {
    void showProblem(AssignmentProblem problem);
    void setPresenter(Presenter presenter);
    static public interface Presenter {
        void setupView(AssProblemView view);
        void showWhiteboard(String title);
        void markSolutionAsComplete();
        InmhItemData getItemData();
    }

    
}

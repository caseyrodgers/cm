package hotmath.gwt.cm_search.client.view;

import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

import com.google.gwt.user.client.ui.IsWidget;

public interface TutorView extends IsWidget {
    void setPresenter(Presenter presenter);
    void loadSolution(SolutionResponse solution);
    static public interface Presenter {
        void prepareView(TopicView view);
        void markSolutionAsComplete();
        void showWhiteboard(String title);
        void goBack();
    }

}
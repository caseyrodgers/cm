package hotmath.gwt.cm_search.client.view;

import com.google.gwt.user.client.ui.IsWidget;

public interface ReviewView extends IsWidget {
    void setPresenter(Presenter presenter);
    void loadLesson(String title, String lesson);
    static public interface Presenter {
    }

}
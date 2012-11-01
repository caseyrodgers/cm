package hotmath.gwt.cm_search.client.view;

import com.google.gwt.user.client.ui.IsWidget;

public interface VideoView extends IsWidget {
    void setPresenter(Presenter presenter);
    void loadVideo(String title, String videoUrl);
    static public interface Presenter {
    }

}
package hotmath.gwt.cm_search.client.view;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface VideoView extends IsWidget {
    void setPresenter(Presenter presenter);
    void loadVideo(String title, Widget viewShared);
    static public interface Presenter {
    }

}
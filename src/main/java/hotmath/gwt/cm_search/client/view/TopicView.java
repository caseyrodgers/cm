package hotmath.gwt.cm_search.client.view;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;

import com.google.gwt.user.client.ui.IsWidget;

public interface TopicView extends IsWidget {
    void showTopic(String topicFile);
    void setPresenter(Presenter presenter);
    void setLesson(PrescriptionSessionData lesson);

    static public interface Presenter {
        void prepareView(TopicView view);
        void loadResource(InmhItemData resourceItem);
    }

}
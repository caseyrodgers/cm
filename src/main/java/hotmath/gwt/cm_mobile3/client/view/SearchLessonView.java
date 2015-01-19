package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;

public interface SearchLessonView extends IPage {
    void setPresenter(Presenter presenter);
    static public interface Presenter {
        void loadWhiteboard(ShowWorkPanel2 _showWork, String _lastPid);
        void setupView(SearchLessonView searchLessonView);
        void loadResource(InmhItemData resourceItem);
    }
    void showWhiteboard(String pid);
    void loadLesson(PrescriptionSessionData data);
}
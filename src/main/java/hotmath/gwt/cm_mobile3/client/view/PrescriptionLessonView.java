package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;

public interface PrescriptionLessonView extends IPage {
    void setPresenter(Presenter presenter);
    void setLesson(PrescriptionSessionData lesson);
    void refreshRppIndicators();
    static public interface Presenter {
        void prepareView(PrescriptionLessonView view);
        void loadResource(InmhItemData resourceItem);
        void moveToNextLesson(PrescriptionLessonView view);
        void moveToPreviousLesson(PrescriptionLessonView view);
        void showLessonChooser();
    }
}
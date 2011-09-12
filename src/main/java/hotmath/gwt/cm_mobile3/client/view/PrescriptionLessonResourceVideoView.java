package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import com.google.gwt.event.shared.EventBus;

public interface PrescriptionLessonResourceVideoView extends IPage {
    void setPresenter(Presenter presenter);
    void setVideoUrlWithOutExtension(String nonFlashVideoUrl);
    void setVideoTitle(String title);
    static public interface Presenter {
        void setupView(PrescriptionLessonResourceVideoView view);
        EventBus getEventBus();
    }

}

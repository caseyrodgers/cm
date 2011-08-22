package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import com.google.gwt.user.client.ui.Widget;

public interface PrescriptionLessonResourceView extends IPage {
    
    void setPresenter(Presenter presenter);
    void setResourceTitle(String title);
    void setResourceWidget(Widget widget);

    public interface Presenter {
        void setupView(PrescriptionLessonResourceView view);
    }
}

package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

public interface PrescriptionLessonResourceReviewView extends IPage {
    void setPresenter(Presenter presenter);
    void setTitle(String title);
    void setReviewHtml(String html);
    static public interface Presenter {
        void setupView(PrescriptionLessonResourceReviewView view);
    }
}

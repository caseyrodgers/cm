package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

public interface PrescriptionLessonResourceReviewView extends IPage {
    void setPresenter(Presenter presenter);
    void setHeaderTitle(String title);
    void setReviewHtml(String html);
    static public interface Presenter {
        void setupView(PrescriptionLessonResourceReviewView view);
        void loadLesson(PrescriptionLessonResourceReviewViewImpl prescriptionLessonResourceReviewViewImpl, boolean isSpanish, CallbackOnComplete callbackOnComplete);
    }
    void loadLesson(String title, String lesson);
}

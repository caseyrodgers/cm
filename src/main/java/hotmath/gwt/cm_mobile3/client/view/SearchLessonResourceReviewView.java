package hotmath.gwt.cm_mobile3.client.view;


import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

public interface SearchLessonResourceReviewView extends IPage {
    void setPresenter(Presenter presenter);
    void setHeaderTitle(String title);
    void setReviewHtml(String html);
    static public interface Presenter {
        void setupView(SearchLessonResourceReviewView view);
        void loadLesson(SearchLessonResourceReviewView view, boolean isSpanish, CallbackOnComplete callback);
    }
    void loadLesson(String title, String lesson);
}

package hotmath.gwt.cm_mobile_shared.client.activity;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

public interface LessonView extends IPage {
    void setPresenter(Presenter pres, CallbackOnComplete callbackOnComplete);
    public interface Presenter {
        void loadLesson(LessonView lessonViewImpl, CallbackOnComplete callback);
    }
    void loadLesson(String lessonName, String lessonHtml);
}

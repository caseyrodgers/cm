package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

public interface PrescriptionLessonResourceResultsView extends IPage {
    void setPresenter(Presenter p);
    void setQuizResults(String title, String resultHtml, String resultJson, int questionCount, int correctCount);
    static public interface Presenter {
        void setupView(PrescriptionLessonResourceResultsView view);
    }
}

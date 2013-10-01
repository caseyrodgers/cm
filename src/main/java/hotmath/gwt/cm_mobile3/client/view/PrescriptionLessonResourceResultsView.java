package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;

public interface PrescriptionLessonResourceResultsView extends IPage {
    void setPresenter(Presenter p);
    void setQuizResults(String title, String resultHtml, String resultJson, int questionCount, int correctCount);
    static public interface Presenter {
        void setupView(PrescriptionLessonResourceResultsView view);
        void loadWhiteboard(ShowWorkPanel2 showWork, String pid);
    }
    void showWhiteboard(String pid);
}

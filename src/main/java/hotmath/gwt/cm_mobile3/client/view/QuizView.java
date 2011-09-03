package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.HasWhiteboard;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;

public interface QuizView extends IPage, HasWhiteboard {
    void setPresenter(Presenter presenter);
    void setQuizHtml(String quizHtml, int questionCount);
    void clearQuizHtml();
    static public interface Presenter {
        void prepareQuizView(QuizView quizView);
        void checkQuiz();
        void showWork();
    }
}

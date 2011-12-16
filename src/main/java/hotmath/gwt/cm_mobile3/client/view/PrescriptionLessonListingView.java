package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;

import java.util.List;

public interface PrescriptionLessonListingView extends IPage {
    
    void setPresenter(Presenter presenter);
    void setLessonListing(List<SessionTopic> lessons);
    void showNextQuizButton(boolean trueFalse);
    void hideInfoAboutNextQuiz();
    
    static public interface Presenter {
        void setupView(PrescriptionLessonListingView view);
        void loadLesson(int sessionNum);
        void moveToNextSegment();
        String getStatusForLesson(String lesson);
    }
}

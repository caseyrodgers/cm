package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile3.client.view.EndOfProgramView;
import hotmath.gwt.cm_mobile3.client.view.EndOfProgramViewImpl;
import hotmath.gwt.cm_mobile3.client.view.LoginView;
import hotmath.gwt.cm_mobile3.client.view.LoginViewImpl;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonListingView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonListingViewImpl;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceResultsView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceResultsViewImpl;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceReviewView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceReviewViewImpl;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorViewImpl;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceViewImpl;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonViewImpl;
import hotmath.gwt.cm_mobile3.client.view.QuizView;
import hotmath.gwt.cm_mobile3.client.view.QuizViewImpl;
import hotmath.gwt.cm_mobile3.client.view.SearchView;
import hotmath.gwt.cm_mobile3.client.view.SearchViewImpl;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile3.client.view.WelcomeViewImpl;
import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;
import hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoViewImpl;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkView;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkViewImpl;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class ClientFactoryImplBase implements ClientFactory {

    EventBus eventBus = new SimpleEventBus();

    PagesContainerPanel pagesContainer = new PagesContainerPanelImplIPad();
    WelcomeView welcomeView = new WelcomeViewImpl();
    LoginView loginView = new LoginViewImpl();
    QuizView quizView = new QuizViewImpl();
    ShowWorkView showWorkView = new ShowWorkViewImpl();
    PrescriptionLessonView prescriptionView = new PrescriptionLessonViewImpl();
    PrescriptionLessonResourceView resourceView = new PrescriptionLessonResourceViewImpl();
    PrescriptionLessonResourceReviewView reviewView = new PrescriptionLessonResourceReviewViewImpl();
    PrescriptionLessonResourceVideoView videoView = new PrescriptionLessonResourceVideoViewImpl();
    PrescriptionLessonResourceTutorView tutorView = new PrescriptionLessonResourceTutorViewImpl();
    PrescriptionLessonResourceResultsView resultsView = new PrescriptionLessonResourceResultsViewImpl();
    PrescriptionLessonListingView lessonListingView = new PrescriptionLessonListingViewImpl();    
    EndOfProgramView endOfProgramView = new EndOfProgramViewImpl();
    SearchView searchView = new SearchViewImpl();
    
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PagesContainerPanel getPagesContainer() {
        return pagesContainer;
    }
    
    @Override
    public WelcomeView getWelcomeView() {
        return welcomeView;
    }

    @Override
    public LoginView getLoginView() {
        return loginView;
    }

    @Override
    public QuizView getQuizView() {
        return quizView;
    }

    @Override
    public ShowWorkView getShowWorkView() {
        return showWorkView;
    }

    @Override
    public PrescriptionLessonView getPrescriptionLessonView() {
        return prescriptionView;
    }
    
    @Override
    public PrescriptionLessonResourceView getPrescriptionLessonResourceView() {
        return resourceView;
    }

    @Override
    public PrescriptionLessonResourceReviewView getPrescriptionLessonResourceReviewView() {
        return reviewView;
    }

    @Override
    public PrescriptionLessonResourceVideoView getPrescriptionLessonResourceVideoView() {
        return videoView;
    }

    @Override
    public PrescriptionLessonResourceTutorView getPrescriptionLessonResourceTutorView() {
        return tutorView;
    }

    @Override
    public PrescriptionLessonResourceResultsView getPrescriptionLessonResourceResultsView() {
        return resultsView;
    }

    @Override
    public PrescriptionLessonListingView getPrescriptionLessonListingView() {
        return lessonListingView;
    }

    @Override
    public EndOfProgramView getEndOfProgramView() {
        return endOfProgramView;
    }

    @Override
    public SearchView getSearchView() {
        return searchView;
    }
}

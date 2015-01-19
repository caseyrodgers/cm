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
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceWebLinkView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonSearchView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonSearchViewImpl;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonViewImpl;
import hotmath.gwt.cm_mobile3.client.view.QuizView;
import hotmath.gwt.cm_mobile3.client.view.QuizViewImpl;
import hotmath.gwt.cm_mobile3.client.view.SearchLessonResourceReviewView;
import hotmath.gwt.cm_mobile3.client.view.SearchLessonResourceReviewViewImpl;
import hotmath.gwt.cm_mobile3.client.view.SearchView;
import hotmath.gwt.cm_mobile3.client.view.SearchViewImpl;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile3.client.view.WelcomeViewImpl;
import hotmath.gwt.cm_mobile_shared.client.PagesContainerPanelImplIPad;
import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentListView;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentListViewImpl;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentProblemView;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentProblemViewImpl;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentShowWorkView;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentShowWorkViewImpl;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentView;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentViewImpl;
import hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoViewImpl;
import hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceWebLinkViewImpl;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkView;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkViewImpl;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.google.gwt.event.shared.EventBus;

public class ClientFactoryImplBase implements ClientFactory {

    EventBus eventBus = CmRpcCore.EVENT_BUS; // app level event bus
    PagesContainerPanel pagesContainer = new PagesContainerPanelImplIPad();
    WelcomeView welcomeView;
    LoginView loginView;
    QuizView quizView;
    ShowWorkView showWorkView ;
    PrescriptionLessonView prescriptionView;
    
    PrescriptionLessonResourceView resourceView;
    PrescriptionLessonResourceReviewView reviewView;
    PrescriptionLessonResourceVideoView videoView;
    PrescriptionLessonResourceTutorView tutorView;
    PrescriptionLessonResourceResultsView resultsView;
    PrescriptionLessonListingView lessonListingView;
    PrescriptionLessonResourceWebLinkView webLinkView;
    PrescriptionLessonSearchView searchLessonView;
    
    EndOfProgramView endOfProgramView;
    SearchView searchView;
    AssignmentProblemView problemView;

    private AssignmentListView assignmentListView;
    private AssignmentView assignmentView;
    private AssignmentShowWorkView assShowView;
    private SearchLessonResourceReviewView searchLessonResourceReviewView;
    
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
        if(welcomeView == null) {
            welcomeView = new WelcomeViewImpl(); 
        }
        return welcomeView;
    }

    @Override
    public LoginView getLoginView() {
        if(loginView == null) {
            loginView = new LoginViewImpl();
        }
        return loginView;
    }

    @Override
    public QuizView getQuizView() {
        if(quizView == null) {
            quizView = new QuizViewImpl();
        }
        return quizView;
    }

    @Override
    public ShowWorkView getShowWorkView() {
        if(showWorkView == null) {
            showWorkView = new ShowWorkViewImpl();
        }
        return showWorkView;
    }

    @Override
    public PrescriptionLessonView getPrescriptionLessonView() {
        if(prescriptionView == null) {
            prescriptionView = new PrescriptionLessonViewImpl();
        }
        return prescriptionView;
    }
    
    @Override
    public PrescriptionLessonResourceView getPrescriptionLessonResourceView() {
        if(resourceView == null) {
            resourceView = new PrescriptionLessonResourceViewImpl();
        }
        return resourceView;
    }

    @Override
    public PrescriptionLessonResourceReviewView getPrescriptionLessonResourceReviewView() {
        if(reviewView == null) {
            reviewView = new PrescriptionLessonResourceReviewViewImpl();
        }
        return reviewView;
    }

    @Override
    public PrescriptionLessonResourceVideoView getPrescriptionLessonResourceVideoView() {
        if(videoView == null) {
            videoView = new PrescriptionLessonResourceVideoViewImpl();
        }
        return videoView;
    }

    @Override
    public PrescriptionLessonResourceTutorView getPrescriptionLessonResourceTutorView() {
        if(tutorView == null) {
            tutorView = new PrescriptionLessonResourceTutorViewImpl();
        }
        return tutorView;
    }

    @Override
    public PrescriptionLessonResourceResultsView getPrescriptionLessonResourceResultsView() {
        if(resultsView == null) {
            resultsView = new PrescriptionLessonResourceResultsViewImpl();
        }
        return resultsView;
    }

    @Override
    public PrescriptionLessonListingView getPrescriptionLessonListingView() {
        if(lessonListingView == null) {
            lessonListingView  = new PrescriptionLessonListingViewImpl();
        }
        return lessonListingView;
    }

    @Override
    public EndOfProgramView getEndOfProgramView() {
        if(endOfProgramView == null) {
            endOfProgramView  = new EndOfProgramViewImpl();
        }
        return endOfProgramView;
    }

    @Override
    public SearchView getSearchView() {
        if(searchView == null) {
            searchView = new SearchViewImpl();
        }
        return searchView;
    }
    
    @Override
    public AssignmentListView getAssignmentListView() {
        if(this.assignmentListView == null) {
            this.assignmentListView = new AssignmentListViewImpl();
        }
        return this.assignmentListView;
    }

    @Override
    public AssignmentView getAssignmentView() {
        if(this.assignmentView == null) {
            this.assignmentView = new AssignmentViewImpl();
        }
        return this.assignmentView;
    }

    @Override
    public AssignmentProblemView getAssignmentProblemView() {
        if(problemView == null) {
            problemView = new AssignmentProblemViewImpl();
        }
        return problemView;
    }

    @Override
    public AssignmentShowWorkView getAssignmentShowworkView() {
        if(assShowView == null) {
            assShowView = new AssignmentShowWorkViewImpl();
        }
        return assShowView;
    }

    @Override
    public PrescriptionLessonResourceWebLinkView getPrescriptionLessonResourceWebLinkView() {
        if(webLinkView == null) {
            webLinkView = new PrescriptionLessonResourceWebLinkViewImpl();
        }
        return webLinkView;
    }

    @Override
    public PrescriptionLessonSearchView getPrescriptionLessonSearchView() {
        if(searchLessonView == null) {
            searchLessonView = new PrescriptionLessonSearchViewImpl();
        }
        return searchLessonView;
    }

    @Override
    public SearchLessonResourceReviewView getSearchLessonResourceReviewView() {
        if(searchLessonResourceReviewView == null) {
            searchLessonResourceReviewView = new SearchLessonResourceReviewViewImpl();
        }
        return searchLessonResourceReviewView;
    }
}

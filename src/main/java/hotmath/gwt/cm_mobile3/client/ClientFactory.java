package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile3.client.view.EndOfProgramView;
import hotmath.gwt.cm_mobile3.client.view.LoginView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonListingView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceResultsView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceReviewView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceWebLinkView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonView;
import hotmath.gwt.cm_mobile3.client.view.QuizView;
import hotmath.gwt.cm_mobile3.client.view.SearchView;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentListView;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentProblemView;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentShowWorkView;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentView;
import hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkView;

import com.google.gwt.event.shared.EventBus;


public interface ClientFactory {
    EventBus getEventBus();
    PagesContainerPanel getPagesContainer();
    LoginView getLoginView();
    QuizView getQuizView();
    WelcomeView getWelcomeView();
    ShowWorkView getShowWorkView();
    PrescriptionLessonView getPrescriptionLessonView();
    PrescriptionLessonResourceView getPrescriptionLessonResourceView();
    PrescriptionLessonResourceReviewView getPrescriptionLessonResourceReviewView();
    PrescriptionLessonResourceVideoView getPrescriptionLessonResourceVideoView();
    PrescriptionLessonResourceTutorView getPrescriptionLessonResourceTutorView();
    PrescriptionLessonResourceResultsView getPrescriptionLessonResourceResultsView();
    PrescriptionLessonListingView getPrescriptionLessonListingView();
    SearchView getSearchView();
    EndOfProgramView getEndOfProgramView();
    AssignmentListView getAssignmentListView();
    AssignmentView getAssignmentView();
    AssignmentProblemView getAssignmentProblemView();
    AssignmentShowWorkView getAssignmentShowworkView();
    PrescriptionLessonResourceWebLinkView getPrescriptionLessonResourceWebLinkView();
}
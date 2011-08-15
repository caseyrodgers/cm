package hotmath.gwt.hm_mobile.client;

import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;
import hotmath.gwt.hm_mobile.client.view.BookListView;
import hotmath.gwt.hm_mobile.client.view.BookSearchView;
import hotmath.gwt.hm_mobile.client.view.BookView;
import hotmath.gwt.hm_mobile.client.view.CategoryListView;
import hotmath.gwt.hm_mobile.client.view.HelpView;
import hotmath.gwt.hm_mobile.client.view.HomeView;
import hotmath.gwt.hm_mobile.client.view.MainMobileView;
import hotmath.gwt.hm_mobile.client.view.TutorView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory {
    EventBus getEventBus();
    PlaceController getPlaceController();
    HomeView getHomeView();
    BookListView getBookListView();
    CategoryListView getCategoryListView();
    MainMobileView getMainMobileView();
    BookView getBookView();
    TutorView getTutorView();
    BookSearchView getBookSearchView();
    HelpView getHelpView();
    PagesContainerPanel getPagesContainer();
}
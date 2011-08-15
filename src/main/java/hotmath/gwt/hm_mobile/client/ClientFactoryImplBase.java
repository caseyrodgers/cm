package hotmath.gwt.hm_mobile.client;

import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;
import hotmath.gwt.hm_mobile.client.view.BookListView;
import hotmath.gwt.hm_mobile.client.view.BookListViewImpl;
import hotmath.gwt.hm_mobile.client.view.BookSearchView;
import hotmath.gwt.hm_mobile.client.view.BookSearchViewImpl;
import hotmath.gwt.hm_mobile.client.view.BookView;
import hotmath.gwt.hm_mobile.client.view.BookViewImpl;
import hotmath.gwt.hm_mobile.client.view.CategoryListView;
import hotmath.gwt.hm_mobile.client.view.CategoryListViewImpl;
import hotmath.gwt.hm_mobile.client.view.HelpView;
import hotmath.gwt.hm_mobile.client.view.HelpViewImpl;
import hotmath.gwt.hm_mobile.client.view.HomeView;
import hotmath.gwt.hm_mobile.client.view.HomeViewImpl;
import hotmath.gwt.hm_mobile.client.view.MainMobileView;
import hotmath.gwt.hm_mobile.client.view.MainMobileViewImpl;
import hotmath.gwt.hm_mobile.client.view.TutorView;
import hotmath.gwt.hm_mobile.client.view.TutorViewImpl;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactoryImplBase implements ClientFactory {

    EventBus eventBus = new SimpleEventBus();
    PlaceController placeController = new PlaceController(eventBus);
    MainMobileView mainView = new MainMobileViewImpl();
    HomeView homeView = new HomeViewImpl();
    CategoryListView catView = new CategoryListViewImpl();
    BookListView bookListView = new BookListViewImpl();
    BookView bookView = new BookViewImpl();
    TutorView tutorView = new TutorViewImpl();
    BookSearchView searchView = new BookSearchViewImpl();
    HelpView helpView = new HelpViewImpl();
    /** intialize in subclass
     * 
     */
    PagesContainerPanel pagesContainer;
    
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PlaceController getPlaceController() {
        return placeController;
    }

    @Override
    public HomeView getHomeView() {
        return homeView;
    }

    @Override
    public BookListView getBookListView() {
        return bookListView;
    }

    @Override
    public CategoryListView getCategoryListView() {
        return catView;
    }

    @Override
    public MainMobileView getMainMobileView() {
        return mainView;
    }

    @Override
    public BookView getBookView() {
        // TODO Auto-generated method stub
        return bookView;
    }

    @Override
    public TutorView getTutorView() {
        // TODO Auto-generated method stub
        return tutorView;
    }

    @Override
    public BookSearchView getBookSearchView() {
        return searchView;
    }
    
    public HelpView getHelpView() {
		return helpView;
	}

	@Override
	public PagesContainerPanel getPagesContainer() {
		return pagesContainer;
	}

}

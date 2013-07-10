package hotmath.gwt.hm_mobile.client;

import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
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
import hotmath.gwt.hm_mobile.client.view.LoginView;
import hotmath.gwt.hm_mobile.client.view.LoginViewImpl;
import hotmath.gwt.hm_mobile.client.view.MainMobileView;
import hotmath.gwt.hm_mobile.client.view.MainMobileViewImpl;
import hotmath.gwt.hm_mobile.client.view.TutorView;
import hotmath.gwt.hm_mobile.client.view.TutorViewImpl;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactoryImplBase implements ClientFactory {

    EventBus eventBus = CmRpcCore.EVENT_BUS;
    PlaceController placeController = new PlaceController(eventBus);
    MainMobileView mainView;
    HomeView homeView;
    CategoryListView catView;
    BookListView bookListView;
    BookView bookView;
    TutorView tutorView;
    BookSearchView searchView;
    HelpView helpView;
    LoginView loginView;

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
        if(homeView == null) {
            homeView = new HomeViewImpl();
        }
        return homeView;
    }

    @Override
    public BookListView getBookListView() {
        if(bookListView == null) {
            bookListView  = new BookListViewImpl();
        }
        return bookListView;
    }

    @Override
    public CategoryListView getCategoryListView() {
        if(catView == null) {
            catView = new CategoryListViewImpl();
        }
        return catView;
    }

    @Override
    public MainMobileView getMainMobileView() {
        if(mainView == null) {
            mainView = new MainMobileViewImpl();
        }
        return mainView;
    }

    @Override
    public BookView getBookView() {
        if(bookView == null) {
            bookView = new BookViewImpl();
        }
        return bookView;
    }

    @Override
    public TutorView getTutorView() {
        if(tutorView == null) {
            tutorView  = new TutorViewImpl();
        }
        return tutorView;
    }

    @Override
    public BookSearchView getBookSearchView() {
        if(searchView == null) {
            searchView  = new BookSearchViewImpl();
        }
        return searchView;
    }
    
    public HelpView getHelpView() {
        if(helpView == null) {
            helpView = new HelpViewImpl();            
        }
		return helpView;
	}

	@Override
	public PagesContainerPanel getPagesContainer() {
		return pagesContainer;
	}

    @Override
    public LoginView getLoginView() {
        if(loginView == null) {
            loginView = new LoginViewImpl();
        }
        return loginView;
    }

}

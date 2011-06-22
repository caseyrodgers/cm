package hotmath.gwt.hm_mobile.client.activity;

import hotmath.gwt.hm_mobile.client.ClientFactory;
import hotmath.gwt.hm_mobile.client.place.BookListPlace;
import hotmath.gwt.hm_mobile.client.place.CategoryListPlace;
import hotmath.gwt.hm_mobile.client.place.HomePlace;
import hotmath.gwt.hm_mobile.client.view.MainMobileView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class MainMobileActivity extends AbstractActivity implements ActivityMapper, MainMobileView.Presenter {
	private ClientFactory clientFactory;
	private EventBus eventBus;
	private ActivityManager activityManager;
	MainMobileView mainView = null;

	public MainMobileActivity(ClientFactory clientFactory, EventBus eventBus) {
		this.clientFactory = clientFactory;
		this.eventBus = eventBus;
		this.activityManager = new ActivityManager(this, eventBus);
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {

		mainView = clientFactory.getMainMobileView();
		mainView.setPresenter(this);
		containerWidget.setWidget(mainView.asWidget());
		activityManager.setDisplay(mainView);
	}

	/**
	 * Ask user before stopping this activity
	 */
	@Override
	public String mayStop() {
		return null; // "Are you sure you want to stop this Yoga Class?";
	}

	/**
	 * Navigate to a new Place in the browser
	 */
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}
	

    @Override
    public Activity getActivity(Place place) {
    	
    	Activity activity = null;
		
    	/** only one place right now */
    	if(place == null || place instanceof HomePlace) {
    		activity = new HomeActivity((HomePlace)place,clientFactory);
        	//mainView.getCenterWidget().setWidget(clientFactory.getHomeView());
    	}
    	else if(place instanceof BookListPlace) {
    		activity = new BookListActivity((BookListPlace)place, clientFactory);
    		//mainView.getCenterWidget().setWidget(clientFactory.getBookListView());
    	}
    	else if(place instanceof CategoryListPlace) {
    		activity = new CategoryListActivity((CategoryListPlace)place, clientFactory);
    		//mainView.getCenterWidget().setWidget(clientFactory.getCategoryListView());
    	}
    	else  {
    		assert(false);
    	}
    	//mainView.getCenterWidget().setWidget(new Label("Test"));
    	return activity;
    }

	@Override
    public void navigateToBook() {
		//CategoryListView listView = HmMobile.__clientFactory.getCategoryListView();
		///CategoryListActivity activity = new CategoryListActivity(new CategoryListPlace(""), HmMobile.__clientFactory);
		//listView.setPresenter(activity);
		//((Page)clientFactory.getMainMobileView()).goTo((Page)listView);
	}

	@Override
    public void searchForBook() {
		//BookSearchView searchVIew = HmMobile.__clientFactory.getBookSearchView();
		//BookSearchActivity activity = new BookSearchActivity(new MobilePlace(""), HmMobile.__clientFactory);
		//searchVIew.setPresenter(activity);
		//searchVIew.initialize();
		
		//((Page)clientFactory.getMainMobileView()).goTo((Page)searchVIew);		
    }

}
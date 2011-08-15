package hotmath.gwt.hm_mobile.client.activity;

import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.ClientFactory;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.persist.HmMobilePersistedPropertiesManager;
import hotmath.gwt.hm_mobile.client.place.BookListPlace;
import hotmath.gwt.hm_mobile.client.place.BookSearchPlace;
import hotmath.gwt.hm_mobile.client.rpc.BookSearchAction;
import hotmath.gwt.hm_mobile.client.view.BookListView;
import hotmath.gwt.hm_mobile.client.view.BookSearchView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class BookSearchActivity extends AbstractActivity implements BookSearchView.Presenter {
	
	private ClientFactory clientFactory;
	private String token;

	public BookSearchActivity(BookSearchPlace place, ClientFactory clientFactory) {
		this.token = place.getToken();
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		BookListView view = clientFactory.getBookListView();
		containerWidget.setWidget(view.asWidget());
		view.showBookList(null, null);		
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
    public void doBookSearch(String searchFor) {
		HmMobilePersistedPropertiesManager.setSearchTerm(searchFor);
		
		clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(true));
		
		BookSearchAction action = new BookSearchAction(searchFor);
		HmMobile.getCmService().execute(action,new AsyncCallback<CmList<BookModel>>() {
			public void onSuccess(CmList<BookModel> bookList) {
				
				clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
				
				if(bookList.size() == 0) {
					Window.alert("No matches");
				}
				else {
					BookListView bookListView = clientFactory.getBookListView();
					BookListActivity act = new BookListActivity(new BookListPlace(""), clientFactory);
					bookListView.setPresenter(act);
					
					bookListView.showBookList("Seached", bookList);
					
					clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)bookListView));
				}
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
				arg0.printStackTrace();
				Window.alert(arg0.getMessage());
			}
		});
    }
}
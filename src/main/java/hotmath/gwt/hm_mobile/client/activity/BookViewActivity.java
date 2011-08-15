package hotmath.gwt.hm_mobile.client.activity;

import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.ClientFactory;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.event.ShowTutorViewEvent;
import hotmath.gwt.hm_mobile.client.model.BookInfoModel;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.persist.HmMobilePersistedPropertiesManager;
import hotmath.gwt.hm_mobile.client.place.BookViewPlace;
import hotmath.gwt.hm_mobile.client.rpc.GetBookInfoAction;
import hotmath.gwt.hm_mobile.client.rpc.GetProblemNumbersAction;
import hotmath.gwt.hm_mobile.client.view.BookListView;
import hotmath.gwt.hm_mobile.client.view.BookView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class BookViewActivity extends AbstractActivity implements BookView.Presenter {
	private ClientFactory clientFactory;
	private String token;

	public BookViewActivity(BookViewPlace place, ClientFactory clientFactory) {
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
    public void loadBookInfo(final BookModel book, final CallbackOnComplete callback) {
		
		
		clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(true));

		GetBookInfoAction action = new GetBookInfoAction(book);
		HmMobile.getCmService().execute(action,new AsyncCallback<BookInfoModel>() {
			public void onSuccess(BookInfoModel bookInfo) {

			     int page=book.getPage();
			     if(page == 0) {
			    	 Integer opage = HmMobilePersistedPropertiesManager.getInstance().getBookPages().get(book.getTextCode());
			    	 if(opage != null) {
			    		 page = opage;
			    	 }
			     }
			        
				BookView bookView = clientFactory.getBookView();
				bookView.showBook(bookInfo.getBook(), bookInfo, page);		
				
				
			     HmMobilePersistedPropertiesManager.setLastBookPlace(book,page);


				callback.isComplete();
				clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
			}
			
			
			@Override
			public void onFailure(Throwable arg0) {
				clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));

				arg0.printStackTrace();
				Window.alert(arg0.getMessage());
			}
		});
    }

	@Override
    public void getProblemNumbers(BookModel book, int page) {
		clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(true));

		GetProblemNumbersAction action = new GetProblemNumbersAction(book, page);
		HmMobile.getCmService().execute(action,new AsyncCallback<CmList<ProblemNumber>>() {
			public void onSuccess(CmList<ProblemNumber> problems) {
				clientFactory.getBookView().showProblemNumbers(problems);
				clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));

				arg0.printStackTrace();
				Window.alert(arg0.getMessage());
			}
		});
    }

	@Override
    public void loadSolution(final ProblemNumber problem) {
	    HmMobile.__clientFactory.getEventBus().fireEvent(new ShowTutorViewEvent(problem));
    }
}
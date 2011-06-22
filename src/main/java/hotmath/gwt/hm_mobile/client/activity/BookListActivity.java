package hotmath.gwt.hm_mobile.client.activity;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.ClientFactory;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.model.CategoryModel;
import hotmath.gwt.hm_mobile.client.place.BookListPlace;
import hotmath.gwt.hm_mobile.client.rpc.GetBooksAction;
import hotmath.gwt.hm_mobile.client.view.BookListView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class BookListActivity extends AbstractActivity implements BookListView.Presenter {
    private ClientFactory clientFactory;
    private String token;

    public BookListActivity(BookListPlace place, ClientFactory clientFactory) {
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
        view.showBookList(null);
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
    public void doLoadBookForSubject(String subject) {
        CatchupMathMobileShared.__instance.showBusyPanel();

        try {
            GetBooksAction action = new GetBooksAction(new CategoryModel(subject));
            HmMobile.getCmService().execute(action, new AsyncCallback<CmList<BookModel>>() {
                @Override
                public void onSuccess(CmList<BookModel> books) {
                    BookListView bookView = clientFactory.getBookListView();
                    bookView.showBookList(books);
                    CatchupMathMobileShared.__instance.hideBusyPanel();
                }

                @Override
                public void onFailure(Throwable arg0) {
                    CatchupMathMobileShared.__instance.hideBusyPanel();

                    arg0.printStackTrace();
                    Window.alert("Server error: " + arg0);
                }
            });
        } catch (Error e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    }
}
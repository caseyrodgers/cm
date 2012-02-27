package hotmath.gwt.cm_search.client.activity;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_search.client.ClientFactory;
import hotmath.gwt.cm_search.client.places.SearchPlace;
import hotmath.gwt.cm_search.client.places.TopicPlace;
import hotmath.gwt.cm_search.client.view.SearchView;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class SearchActivity extends AbstractActivity implements SearchView.Presenter {
    
    private SearchPlace place;
    private ClientFactory clientFactory;
    
    public SearchActivity(SearchPlace place, ClientFactory clientFactory) {
        this.place = place;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        final SearchView view = clientFactory.getSearchView();
        view.setPresenter(this);
        panel.setWidget(view);
        
        
        if(place != null && place.getToken() != null && place.getToken().length() > 2) {
            doSearch(place.getToken(),new CallBack() {
                @Override
                public void searchResults(CmList<Topic> results) {
                    view.showSearchResults(place.getToken(),results);
                }
            });
        }
    }
    
    @Override
    public void doSearch(String search, final CallBack callBack) {
        if(search == null || search.length() < 3) {
            MessageBox.showError("Search string must be at least three letters.");
            return;
        }
        
        Log.info("Doing search: " + search);
        
        SearchTopicAction action = new SearchTopicAction(search);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmList<Topic>>() {
            @Override
            public void onSuccess(CmList<Topic> result) {
                callBack.searchResults(result);
            }
            
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error: " + caught);
            }
        });
    }

    @Override
    public void loadTopic(String topicFile) {
        clientFactory.getPlaceContainer().goTo(new TopicPlace(topicFile));
    }
    
    
    static public interface CallBack {
        void searchResults(CmList<Topic> results);
    }
}

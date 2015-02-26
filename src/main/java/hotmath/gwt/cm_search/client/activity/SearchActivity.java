package hotmath.gwt.cm_search.client.activity;

import hotmath.gwt.cm_core.client.model.TopicSearchResults;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_rpc.client.model.TopicMatch;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchApp;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
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
                public void searchResults(CmList<TopicMatch> results) {
                    view.showSearchResults(place.getToken(),results);
                }
            });
        }
    }
    
    @Override
    public void doSearch(String search, final CallBack callBack) {
        if(search == null || search.length() < 3) {
            PopupMessageBox.showError("Search string must be at least three letters.");
            return;
        }
        
        Log.info("Doing search: " + search);
        
        SearchTopicAction action = new SearchTopicAction(search,SearchApp.SEARCH_STAND_ALONE,0);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<TopicSearchResults>() {
            @Override
            public void onSuccess(TopicSearchResults result) {
                callBack.searchResults(result.getTopics());
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
        void searchResults(CmList<TopicMatch> results);
    }
}

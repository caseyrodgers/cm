package hotmath.gwt.cm_search.client.view;



import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_search.client.activity.SearchActivity.CallBack;

import com.google.gwt.user.client.ui.IsWidget;


public interface SearchView extends IsWidget {
    void showSearch();
    void showSearchResults(String searchedFor, CmList<Topic> results);
    void setPresenter(Presenter presenter);
    
    public interface Presenter {
        void doSearch(String search, CallBack callBack);
        void loadTopic(String topicFile);
    }
}
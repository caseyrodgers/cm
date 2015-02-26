package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.TopicSearchResults;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;



public class SearchTopicAction implements Action<TopicSearchResults> {

    public enum SearchType{
        /** Absolute search for lesson file */
        FILE,
        /** Do a wild/like search against title */
        LESSON_LIKE}
    
    public enum SearchApp {
        CM_STUDENT,CM_ADMIN,CM_MOBILE,SEARCH_STAND_ALONE,TEST
    }
    
    String search;
    private SearchType searchType;
    private SearchApp searchApp;
    int uid;

    public SearchTopicAction() {}

    /** Return the Prescription data for this runid and session
     *
     * @param runId
     * @param sessionNumber
     * @param updateActiveInfo
     */
    public SearchTopicAction(SearchType searchType, SearchApp searchApp, String search, int uid) {
        this.searchType = searchType;
        this.searchApp = searchApp;
        this.search = search;
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    /** Default is to search like in lesson
     * 
     * @param search
     */
    public SearchTopicAction(String search, SearchApp searchApp, int uid) {
        this(SearchType.LESSON_LIKE, searchApp, search, uid);
    }
    
    public SearchApp getSearchApp() {
        return searchApp;
    }

    public void setSearchApp(SearchApp searchApp) {
        this.searchApp = searchApp;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    @Override
    public String toString() {
        return "SearchTopicAction [search=" + search + ", searchType=" + searchType + "]";
    }

}


package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;



public class SearchTopicAction implements Action<CmList<Topic>> {

    public enum SearchType{
        /** Absolute search for lesson file */
        FILE,
        /** Do a wild/like search against title */
        LESSON_LIKE}
    
    String search;
    private SearchType searchType;

    public SearchTopicAction() {}

    /** Return the Prescription data for this runid and session
     *
     * @param runId
     * @param sessionNumber
     * @param updateActiveInfo
     */
    public SearchTopicAction(SearchType searchType, String search) {
        this.searchType = searchType;
        this.search = search;
    }

    /** Default is to search like in lesson
     * 
     * @param search
     */
    public SearchTopicAction(String search) {
        this(SearchType.LESSON_LIKE, search);
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


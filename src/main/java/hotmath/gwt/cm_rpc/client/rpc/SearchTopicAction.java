package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;



public class SearchTopicAction implements Action<CmList<Topic>> {

    
    String search;

    public SearchTopicAction() {}

    /** Return the Prescription data for this runid and session
     *
     * @param runId
     * @param sessionNumber
     * @param updateActiveInfo
     */
    public SearchTopicAction(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return "SearchTopicAction [search=" + search + "]";
    }

}


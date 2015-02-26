package hotmath.gwt.cm_core.client.model;

import java.io.Serializable; 

import hotmath.gwt.cm_rpc.client.model.TopicMatch;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class TopicSearchResults implements Response {
    
    CmList<TopicMatch> topics;
    CmList<SearchSuggestion> suggestions;

    public TopicSearchResults() {}
    
    public TopicSearchResults(CmList<TopicMatch> topics, CmList<SearchSuggestion> suggestions) {
        this.topics = topics;
        this.suggestions = suggestions;
    }

    public CmList<TopicMatch> getTopics() {
        return topics;
    }

    public void setTopics(CmList<TopicMatch> topics) {
        this.topics = topics;
    }

    public CmList<SearchSuggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(CmList<SearchSuggestion> suggestions) {
        this.suggestions = suggestions;
    }

}

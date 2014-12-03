package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class TopicMatch implements Response {
    
    Topic topic;
    MatchWeight matchWeight;
    
    /** in decending weighted order */
    public static enum MatchWeight {
        /** 5 Next, displaying lessons where the first <p> tag contains one or more search words */
        P_TAG_CONTAINS_SOME, 

        /** > 4 -Next, displaying lessons where Title tag contains one or more search words */
        TITLE_CONTAINS_SOME,

        /** 3 -Next, displaying lessons where the first <p> tag contains all search words */
        P_TAG_CONTAINS_ALL,

        /** 2 Next, displaying lessons where the Title tag contains all search words along with some other words */
       TITLE_HAS_ALL_PLUS,
       
       /** 1. title match search absolute */
       TITLE_HAS_ONLY_SEARCH 

       };
    
    public TopicMatch() {}
    
    
    public TopicMatch(Topic topic, MatchWeight matchWeight) {
        this.topic = topic;
        this.matchWeight = matchWeight;
    }
    
    public String getTopicName() {
        
        if(CmGwtUtils.getQueryParameter("debug") == null) {
            return topic.getName();
        }
        else {
            //  return value = "[" + matchWeight + "]" + topic.getName();
            return topic.getName() + " [" + matchWeight + "]    ";
        }
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public MatchWeight getMatchWeight() {
        return matchWeight;
    }

    public void setMatchWeight(MatchWeight matchWeight) {
        this.matchWeight = matchWeight;
    }
}

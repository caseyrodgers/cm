package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class TopicMatch implements Response {
    
    Topic topic;
    MatchWeight matchWeight;
    
    /** in descending weighted order */
    public static enum MatchWeight {
        /** 6 Next, displaying lessons where the first <p> tag contains one or more search words */
        CONTENT_MATCH_SOME,
        
        /** > 5 -Next, displaying lessons where Title tag contains one or more search words */
        TITLE_MATCH_SOME,

        /** 4 -Next, displaying lessons where the first <p> tag contains all search words */
        CONTENT_MATCH_ALL,

        /** 3 Next, displaying lessons where the Title tag contains all search words along with some other words */
       TITLE_MATCH_ALL_PLUS,
       
       /** 2. title match search absolute */
       TITLE_MATCH_ABSOLUTE, 
       
       /** 1. matches content of solution */              
       SOLUTION_CONTENT_MATCH,
       
       /** 0. matches simple text instring */
       CONTENT_MATCH_SIMPLE

       };
    
    public TopicMatch() {}
    
    
    public TopicMatch(Topic topic, MatchWeight matchWeight) {
        this.topic = topic;
        this.matchWeight = matchWeight;
    }
    
    public String getTopicName() {
        return topic.getName();
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

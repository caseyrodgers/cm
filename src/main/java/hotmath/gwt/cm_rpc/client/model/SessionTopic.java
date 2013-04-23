package hotmath.gwt.cm_rpc.client.model;


import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/**
 * Parent session data, containing a single active session used to encapsulate
 * session data
 * 
 * 
 * @author Casey
 * 
 */
public class SessionTopic implements Response {
    String topic;
    boolean complete;
    String topicStatus;
    
    public SessionTopic() {
        
    }
    public SessionTopic(String topic,boolean complete) {
        this.topic = topic;
        this.complete = complete;
    }
    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public boolean isComplete() {
        return complete;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    @Override
    public String toString() {
        return topic;
    }
    public String getTopicStatus() {
        return topicStatus;
    }
    public void setTopicStatus(String topicStatus) {
        this.topicStatus = topicStatus;
    }
}

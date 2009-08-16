package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.shared.client.rpc.Action;

public class GetStateStandardsAction implements Action<CmList<String>> {
    String topic;
    public GetStateStandardsAction() {}
    
    public GetStateStandardsAction(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "GetStateStandards [topic=" + topic + "]";
    }
}
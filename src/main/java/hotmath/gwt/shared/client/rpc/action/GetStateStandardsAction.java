package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

public class GetStateStandardsAction implements Action<CmList<String>> {
    String topic;
    String state;
    public GetStateStandardsAction() {}
    
    public GetStateStandardsAction(String topic,String state) {
        this.topic = topic;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "GetStateStandardsAction [state=" + state + ", topic=" + topic + "]";
    }
}
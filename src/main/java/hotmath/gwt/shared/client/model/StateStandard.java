package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class StateStandard implements Response {
    
    private String topic;
    private String standardName;
    private String standardNameNew;

    public StateStandard(){}
    
    public StateStandard(String topic, String codeName, String codeNameNew) {
        this.topic = topic;
        this.standardName = codeName;
        this.standardNameNew = codeNameNew;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    public String getStandardNameNew() {
        return standardNameNew;
    }

    public void setStandardNameNew(String standardNameNew) {
        this.standardNameNew = standardNameNew;
    }
}

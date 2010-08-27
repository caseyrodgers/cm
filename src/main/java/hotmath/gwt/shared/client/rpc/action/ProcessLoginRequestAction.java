package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;

public class ProcessLoginRequestAction implements Action<UserInfo> {

    String key;
    
    public ProcessLoginRequestAction() {}
    
    public ProcessLoginRequestAction(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    @Override
    public String toString() {
        return "ProcessLoginRequestAction [key=" + key + "]";
    }
}

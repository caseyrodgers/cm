package hotmath.gwt.cm_rpc.client;

import hotmath.gwt.cm_rpc.client.rpc.CmDestination;
import hotmath.gwt.cm_rpc.client.rpc.Response;

public class UserLoginResponse implements Response {
    
    UserInfo userInfo;
    CmDestination nextAction;
    
    public UserLoginResponse() { }
    
    public UserLoginResponse(UserInfo userInfo, CmDestination nextAction) {
        this.userInfo = userInfo;
        this.nextAction = nextAction;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public CmDestination getNextAction() {
        return nextAction;
    }

    public void setNextAction(CmDestination nextAction) {
        this.nextAction = nextAction;
    }
}

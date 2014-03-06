package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CmAdminModel implements Response {
	
	private static final long serialVersionUID = -2998846590065500098L;

	public CmAdminModel() {}
    
    int uid;
    int subscriberId;
    int passCode;

    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public int getSubscriberId() {
        return subscriberId;
    }
    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }
    public int getPassCode() {
        return passCode;
    }
    public void setPassCode(int passCode) {
        this.passCode = passCode;
    }
}

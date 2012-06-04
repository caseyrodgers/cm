package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;

public class GetAccountInfoForAdminUidAction implements Action<AccountInfoModel> {
	private static final long serialVersionUID = -6291455560561288960L;

	private int uid;
    
    public GetAccountInfoForAdminUidAction() {}
    
    public GetAccountInfoForAdminUidAction(int uid) {
        this.uid = uid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }
    
    @Override
    public String toString() {
    	return "GetAccountInfoForAdminUidAction: [ uid: " + uid + " ]";
    }
}

package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;

public class GetAccountInfoForAdminUidAction implements Action<AccountInfoModel> {
    Integer uid;
    
    public GetAccountInfoForAdminUidAction() {}
    
    public GetAccountInfoForAdminUidAction(Integer uid) {
        this.uid = uid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }
}

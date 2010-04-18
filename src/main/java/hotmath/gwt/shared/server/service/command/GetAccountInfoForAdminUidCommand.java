package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.shared.client.rpc.action.GetAccountInfoForAdminUidAction;
import hotmath.lwl.LWLIntegrationManager;

import java.sql.Connection;

public class GetAccountInfoForAdminUidCommand implements ActionHandler<GetAccountInfoForAdminUidAction, AccountInfoModel>{

    @Override
    public AccountInfoModel execute(Connection conn, GetAccountInfoForAdminUidAction action) throws Exception {
        CmAdminDao dao = new CmAdminDao();
        AccountInfoModel aInfo = dao.getAccountInfo(conn, action.getUid());
        if(aInfo.getIsTutoringEnabled()) {
            int minutes = LWLIntegrationManager.getInstance().getRemainingMinute(aInfo.getSubscriberId());
            aInfo.setTutoringMinutes(minutes);
        }
        return aInfo;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAccountInfoForAdminUidAction.class;
    }

}

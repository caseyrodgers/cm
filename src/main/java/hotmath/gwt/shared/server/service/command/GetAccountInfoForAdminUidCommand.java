package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetAccountInfoForAdminUidAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;

public class GetAccountInfoForAdminUidCommand implements ActionHandler<GetAccountInfoForAdminUidAction, AccountInfoModel>{

    @Override
    public AccountInfoModel execute(Connection conn, GetAccountInfoForAdminUidAction action) throws Exception {
        CmAdminDao dao = new CmAdminDao();
        return dao.getAccountInfo(conn, action.getUid());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAccountInfoForAdminUidAction.class;
    }

}

package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataImplDefault;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;

public class GetAdminTrendingDataCommand implements ActionHandler<GetAdminTrendingDataAction, CmAdminTrendingDataI>{

    @Override
    public CmAdminTrendingDataI execute(Connection conn, GetAdminTrendingDataAction action) throws Exception {
        return new CmAdminTrendingDataImplDefault(new CmAdminDao().getTrendingData(conn, action.getAdminId()));
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAdminTrendingDataAction.class;
    }
}

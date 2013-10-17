package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.WebLinkDao;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.rpc.GetWebLinksForAdminAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetWebLinksForAdminCommand implements ActionHandler<GetWebLinksForAdminAction, CmList<WebLinkModel>> {

    @Override
    public CmList<WebLinkModel> execute(Connection conn, GetWebLinksForAdminAction action) throws Exception {
        
        CmList<WebLinkModel> models = new CmArrayList<WebLinkModel>();
        models.addAll(WebLinkDao.getInstance().getAllWebLinksDefinedForAdmin(action.getAdminId(), true));
        return models;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetWebLinksForAdminAction.class;
    }

}

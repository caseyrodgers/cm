package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.WebLinkDao;
import hotmath.gwt.cm_core.client.model.WebLinkConvertedUrlModel;
import hotmath.gwt.cm_rpc.client.rpc.GetWebLinksConvertedUrlAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetWebLinksConvertedUrlCommand implements ActionHandler<GetWebLinksConvertedUrlAction, WebLinkConvertedUrlModel> {

    @Override
    public WebLinkConvertedUrlModel execute(Connection conn, GetWebLinksConvertedUrlAction action) throws Exception {
        return new WebLinkConvertedUrlModel(action.getUrl(), WebLinkDao.getInstance().performLinkConversion(action.getUrl()));
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetWebLinksConvertedUrlAction.class;
    }
}

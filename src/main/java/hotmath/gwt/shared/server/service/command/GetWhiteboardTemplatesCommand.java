package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.StringHolder;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardTemplatesAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.WhiteboardDao;

import java.sql.Connection;

public class GetWhiteboardTemplatesCommand implements ActionHandler<GetWhiteboardTemplatesAction,StringHolder>{


    @Override
    public StringHolder execute(Connection conn, GetWhiteboardTemplatesAction action) throws Exception {
        return new StringHolder(WhiteboardDao.getInstance().getWhiteboardTemplates(action.getAdminId()));
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetWhiteboardTemplatesAction.class;
    }

}


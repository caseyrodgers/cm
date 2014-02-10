package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.ManageWhiteboardTemplatesAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.WhiteboardDao;

import java.sql.Connection;

public class ManageWhiteboardTemplatesCommand implements ActionHandler<ManageWhiteboardTemplatesAction,RpcData>{


    @Override
    public RpcData execute(Connection conn, ManageWhiteboardTemplatesAction action) throws Exception {

        switch(action.getType()) {
        
        case DELETE:
            WhiteboardDao.getInstance().deleteTemplate(action.getAdminId(), action.getTemplateName());            
            break;
            
            default:
                throw new Exception("Unknown type: " + action.getType());
        }
        
        return new RpcData("status=OK");
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ManageWhiteboardTemplatesAction.class;
    }

}


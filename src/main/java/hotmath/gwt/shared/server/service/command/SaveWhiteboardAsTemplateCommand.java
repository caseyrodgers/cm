package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardAsTemplateAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.WhiteboardDao;

import java.sql.Connection;

public class SaveWhiteboardAsTemplateCommand implements ActionHandler<SaveWhiteboardAsTemplateAction,RpcData>{

    @Override
    public RpcData execute(Connection conn, SaveWhiteboardAsTemplateAction action) throws Exception {
        WhiteboardDao.getInstance().saveWhiteboardAsTemplate(action.getUid(), action.getDataUrl());
        return new RpcData("status=OK");
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveWhiteboardAsTemplateAction.class;
    }
    
}


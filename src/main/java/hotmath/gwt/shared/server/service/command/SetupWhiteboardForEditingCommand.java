package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.SetupWhiteboardForEditingAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;

public class SetupWhiteboardForEditingCommand implements ActionHandler<SetupWhiteboardForEditingAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SetupWhiteboardForEditingAction action) throws Exception {
        RpcData data=null;
        switch(action.getSetupType()) {
        case CREATE:
            String pidEdit = CustomProblemDao.getInstance().setupForWhiteboardEditing(action.getPid());
            data = new RpcData("pid_edit=" + pidEdit);
            break;
            
        case SAVE:
            CustomProblemDao.getInstance().commitWhiteboardEditing(action.getPidEdit(), action.getPid());
            data = new RpcData("status=OK");
            break;
        }
        
        return data;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SetupWhiteboardForEditingAction.class;
    }
}

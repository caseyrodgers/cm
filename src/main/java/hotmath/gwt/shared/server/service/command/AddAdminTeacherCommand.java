package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.ui.AddAdminTeacherAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.testset.ha.CustomProblemDao;

import java.sql.Connection;

public class AddAdminTeacherCommand implements ActionHandler<AddAdminTeacherAction, RpcData>, ActionHandlerManualConnectionManagement {


    @Override
    public RpcData execute(Connection conn, AddAdminTeacherAction action) throws Exception {
        CustomProblemDao.getInstance().addNewTeacher(action.getAdminId(), action.getTeacherName());
        return new RpcData("status=OK");
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return AddAdminTeacherAction.class;
    }

}

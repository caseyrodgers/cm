package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.UnassignStudentsFromAssignmentAction;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class UnassignStudentsFromAssignmentCommand implements ActionHandler<UnassignStudentsFromAssignmentAction, RpcData> {


    @Override
    public RpcData execute(Connection conn, UnassignStudentsFromAssignmentAction action) throws Exception {
        AssignmentDao.getInstance().unassignStudents(action.getAssKey(),action.getStudents());
        return new RpcData("status=OK");
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return UnassignStudentsFromAssignmentAction.class;
    }
}

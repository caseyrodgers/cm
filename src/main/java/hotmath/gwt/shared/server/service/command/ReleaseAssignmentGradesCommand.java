package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.ReleaseAssignmentGradesAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class ReleaseAssignmentGradesCommand implements ActionHandler<ReleaseAssignmentGradesAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, ReleaseAssignmentGradesAction action) throws Exception {
        AssignmentDao.getInstance().releaseGradesForAssignment(action.getAssignKey());
        return new RpcData("status=OK");
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ReleaseAssignmentGradesAction.class;
    }
}

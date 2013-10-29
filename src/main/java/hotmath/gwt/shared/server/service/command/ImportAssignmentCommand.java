package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.ImportAssignmentAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class ImportAssignmentCommand implements ActionHandler<ImportAssignmentAction, RpcData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ImportAssignmentAction.class;
    }

    @Override
    public RpcData execute(Connection conn, ImportAssignmentAction action) throws Exception {
        AssignmentDao.getInstance().importAssignment(action.getAid(), action.getGroupToImportInto(), action.getAssignmentToImport());
        return new RpcData("status=OK");
    }
}

package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentWhiteboardData;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentWhiteboardDataCommand implements ActionHandler<GetAssignmentWhiteboardDataAction, AssignmentWhiteboardData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentWhiteboardDataAction.class;
    }

    @Override
    public AssignmentWhiteboardData execute(Connection conn, GetAssignmentWhiteboardDataAction action) throws Exception {
        CmList<WhiteboardCommand> cList = new CmArrayList<WhiteboardCommand>();
        cList.addAll(AssignmentDao.getInstance().getWhiteboardData(action.getUid(),action.getAssignId(),action.getPid()));
        
        AssignmentWhiteboardData data = new AssignmentWhiteboardData(
                AssignmentDao.getInstance().getStudentProblem(action.getUid(), action.getAssignId(),  action.getPid()), cList);
        
        return data;
    }

}

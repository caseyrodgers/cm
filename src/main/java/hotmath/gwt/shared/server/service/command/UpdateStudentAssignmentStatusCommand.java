package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.UpdateStudentAssignmentStatusAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class UpdateStudentAssignmentStatusCommand implements ActionHandler<UpdateStudentAssignmentStatusAction,RpcData>{
    
    @Override
    public RpcData execute(Connection conn, UpdateStudentAssignmentStatusAction action) throws Exception {
        int[] status = AssignmentDao.getInstance().updateStudentAssignmentStatus(action.getStudentAssignment(), action.isReleaseGrades());
        return new RpcData("status=" + status);
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return UpdateStudentAssignmentStatusAction.class;
    }
}

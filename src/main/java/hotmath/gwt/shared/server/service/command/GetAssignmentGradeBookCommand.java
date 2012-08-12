package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.assignment.GradeBookModel;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;
import java.util.List;

public class GetAssignmentGradeBookCommand implements ActionHandler<GetAssignmentGradeBookAction, RpcData> {


    @Override
    public RpcData execute(Connection conn, GetAssignmentGradeBookAction action) throws Exception {
        List<GradeBookModel> gradeBooks = AssignmentDao.getInstance().getAssignmentGradeBook(action.getAssignKey());
        
        return new RpcData("status=OK");
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentGradeBookAction.class;
    }


}

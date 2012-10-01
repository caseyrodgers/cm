package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentProblemStatusAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class SaveAssignmentProblemStatusCommand implements ActionHandler<SaveAssignmentProblemStatusAction, RpcData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return SaveAssignmentProblemStatusAction.class;
    }

    @Override
    public RpcData execute(Connection conn, SaveAssignmentProblemStatusAction action) throws Exception {
       AssignmentDao.getInstance().saveAssignmentProblemStatus(action.getUid(), action.getAssignKey(),action.getPid(), action.getStatus());
       return new RpcData("status=OK");
    }


}

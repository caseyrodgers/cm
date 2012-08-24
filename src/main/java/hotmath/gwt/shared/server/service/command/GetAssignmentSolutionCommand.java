package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;


/** Get the solution requested and mark this solution
 *  as viewed.
 *  
 * @author casey
 *
 */
public class GetAssignmentSolutionCommand implements ActionHandler<GetAssignmentSolutionAction, SolutionInfo> {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentSolutionAction.class;
    }

    @Override
    public SolutionInfo execute(Connection conn, GetAssignmentSolutionAction action) throws Exception {
        SolutionInfo info = new GetSolutionCommand().execute(conn,  new hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction(0,  0, action.getPid()));
    
        AssignmentDao.getInstance().setAssignmentPidStatus(action.getAssignKey(),action.getUid(),action.getPid());
        
        return info;
    }
}

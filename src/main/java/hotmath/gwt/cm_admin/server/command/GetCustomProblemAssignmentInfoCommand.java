package hotmath.gwt.cm_admin.server.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentModel;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetCustomProblemAssignmentInfoAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;


public class GetCustomProblemAssignmentInfoCommand implements ActionHandler<GetCustomProblemAssignmentInfoAction, CmList<AssignmentModel>> {

	@Override
	public CmList<AssignmentModel> execute(Connection conn,GetCustomProblemAssignmentInfoAction action) throws Exception {
		CmList<AssignmentModel> asses = new CmArrayList<AssignmentModel>(AssignmentDao.getInstance().getAssignmentsWithPid(action.getPid()));
		return asses;
	}
	

	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return GetCustomProblemAssignmentInfoAction.class;
	}
}

package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAssignAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAssignResponse;

import java.sql.Connection;

public class GroupManagerAssignCommand implements ActionHandler<GroupManagerAssignAction, GroupManagerAssignResponse>{

	@Override
	public GroupManagerAssignResponse execute(Connection conn,GroupManagerAssignAction action) throws Exception {
		GroupManagerAssignResponse response = new GroupManagerAssignResponse();
		
		switch(action.getType()) {
		case GET_STUDENTS:
			response.setInGroup(CmAdminDao.getInstance().getGroupStudents(conn,action.getGroup()));
			response.setNotInGroup(CmAdminDao.getInstance().getGroupStudentsNotIn(conn,action.getGroup()));
			break;
			
		case SAVE_STUDENTS:
		    CmAdminDao.getInstance().setGroupStudents(conn, action.getGroup(), action.getGroupStudents());
            break;
			
		}
		return response;
	}
	
	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return GroupManagerAssignAction.class;
	}	
}

package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGroupsAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentGroupsCommand implements ActionHandler<GetAssignmentGroupsAction,CmList<GroupDto>>{

    @Override
    public CmList<GroupDto> execute(Connection conn, GetAssignmentGroupsAction action) throws Exception {
        CmList<GroupDto> cmGroup = new CmArrayList<GroupDto>();
        cmGroup.addAll(AssignmentDao.getInstance().getAssignmentGroups(action.getAid()));
        return cmGroup;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentGroupsAction.class;
    }



}

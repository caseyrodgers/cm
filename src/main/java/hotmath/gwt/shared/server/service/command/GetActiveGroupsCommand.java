package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetActiveGroupsAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;


public class GetActiveGroupsCommand implements ActionHandler<GetActiveGroupsAction, CmList<GroupModel>> {

    @Override
    public CmList<GroupModel> execute(final Connection conn, GetActiveGroupsAction action) throws Exception {
        CmAdminDao cma = new CmAdminDao();
        return cma.getActiveGroups(conn, action.getUid());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetActiveGroupsAction.class;
    }

}

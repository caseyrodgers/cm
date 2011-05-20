package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.client.rpc.action.GetActiveGroupsAction;

import java.sql.Connection;
import java.util.List;


public class GetActiveGroupsCommand implements ActionHandler<GetActiveGroupsAction, CmList<GroupInfoModel>> {

    @Override
    public CmList<GroupInfoModel> execute(final Connection conn, GetActiveGroupsAction action) throws Exception {
        CmAdminDao cma = CmAdminDao.getInstance();
        List<GroupInfoModel> list = cma.getActiveGroups(action.getUid());
        
        CmList<GroupInfoModel> listOut = new CmArrayList<GroupInfoModel>();
        listOut.addAll(list);
        return listOut;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetActiveGroupsAction.class;
    }

}

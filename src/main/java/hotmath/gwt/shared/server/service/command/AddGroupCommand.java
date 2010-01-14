package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.AddGroupAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;


/** Create a new Admin Student group
 * 
 * @author casey
 *
 */
public class AddGroupCommand implements ActionHandler<AddGroupAction, GroupInfoModel> {

    @Override
    public GroupInfoModel execute(Connection conn, AddGroupAction action) throws Exception {
        CmAdminDao cma = new CmAdminDao();
        return cma.addGroup(conn, action.getAdminId(), action.getGroup());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return AddGroupAction.class;
    }
}

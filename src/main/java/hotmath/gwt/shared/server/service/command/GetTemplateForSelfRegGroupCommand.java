package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetTemplateForSelfRegGroupAction;

import java.sql.Connection;

public class GetTemplateForSelfRegGroupCommand implements ActionHandler<GetTemplateForSelfRegGroupAction, StudentModelI>{

    @Override
    public StudentModelI execute(Connection conn, GetTemplateForSelfRegGroupAction action) throws Exception {
       StudentModelI sm = CmStudentDao.getInstance().getTemplateForSelfRegGroup(conn, action.getGroupId());
       return sm;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetTemplateForSelfRegGroupAction.class;
    }

}

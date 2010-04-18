package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetTemplateForSelfRegGroupAction;

import java.sql.Connection;

public class GetTemplateForSelfRegGroupCommand implements ActionHandler<GetTemplateForSelfRegGroupAction, StudentModelI>{

    @Override
    public StudentModelI execute(Connection conn, GetTemplateForSelfRegGroupAction action) throws Exception {
       StudentModelI sm = new CmStudentDao().getTemplateForSelfRegGroup(conn, action.getGroupId());
       return sm;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetTemplateForSelfRegGroupAction.class;
    }

}

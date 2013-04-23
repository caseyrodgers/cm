package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.rpc.action.GetSubjectDefinitionsAction;

import java.sql.Connection;

public class GetSubjectDefinitionsCommand implements ActionHandler<GetSubjectDefinitionsAction, CmList<SubjectModel>>, ActionHandlerManualConnectionManagement {

    @Override
    public CmList<SubjectModel> execute(Connection conn, GetSubjectDefinitionsAction action) throws Exception {
            CmAdminDao cma = CmAdminDao.getInstance();
            
            CmList<SubjectModel> list = new CmArrayList<SubjectModel>();
            list.addAll(cma.getSubjectDefinitions(action.getProgId()));
            return list;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSubjectDefinitionsAction.class;
    }

}

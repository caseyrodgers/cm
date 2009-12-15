package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetSubjectDefinitionsAction;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.gwt.shared.server.service.ActionHandlerManualConnectionManagement;

import java.sql.Connection;

public class GetSubjectDefinitionsCommand implements ActionHandler<GetSubjectDefinitionsAction, CmList<SubjectModel>>, ActionHandlerManualConnectionManagement {

    @Override
    public CmList<SubjectModel> execute(Connection conn, GetSubjectDefinitionsAction action) throws Exception {
            CmAdminDao cma = new CmAdminDao();
            
            CmList<SubjectModel> list = new CmArrayList<SubjectModel>();
            list.addAll(cma.getSubjectDefinitions(action.getProgId()));
            return list;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSubjectDefinitionsAction.class;
    }

}

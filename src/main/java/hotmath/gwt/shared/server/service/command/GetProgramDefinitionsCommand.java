package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.GetProgramDefinitionsAction;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.gwt.shared.server.service.ActionHandlerManualConnectionManagement;

import java.sql.Connection;
import java.util.ArrayList;

import org.apache.log4j.Logger;


public class GetProgramDefinitionsCommand implements ActionHandlerManualConnectionManagement, ActionHandler<GetProgramDefinitionsAction,  CmArrayList<SubjectModel>> {

    
    Logger logger = Logger.getLogger(GetProgramDefinitionsCommand.class);

    @Override
    public CmArrayList<SubjectModel> execute(final Connection conn, GetProgramDefinitionsAction action) throws Exception {
        CmArrayList<SubjectModel> cma = new CmArrayList<SubjectModel>();
        CmAdminDao dao = new CmAdminDao();
        ((ArrayList<SubjectModel>)cma).addAll(dao.getSubjectDefinitions(action.getProgId()));
        return cma;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetProgramDefinitionsAction.class;
    }
}

package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.shared.client.rpc.action.GetProgramDefinitionsAction;

import java.sql.Connection;

import org.apache.log4j.Logger;


public class GetProgramDefinitionsCommand implements ActionHandler<GetProgramDefinitionsAction,  CmList<StudyProgramModel>>, ActionHandlerManualConnectionManagement{

    
    Logger logger = Logger.getLogger(GetProgramDefinitionsCommand.class);

    @Override
    public CmList<StudyProgramModel> execute(final Connection conn, GetProgramDefinitionsAction action) throws Exception {
        CmList<StudyProgramModel> cml = new CmArrayList<StudyProgramModel>();
        CmAdminDao dao = CmAdminDao.getInstance();
        cml.addAll(dao.getProgramDefinitions(action.getAdminId()));
        return cml;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetProgramDefinitionsAction.class;
    }
}

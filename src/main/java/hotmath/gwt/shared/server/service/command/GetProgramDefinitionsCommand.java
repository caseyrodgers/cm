package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.shared.client.rpc.action.GetProgramDefinitionsAction;

import java.sql.Connection;

import org.apache.log4j.Logger;


public class GetProgramDefinitionsCommand implements ActionHandler<GetProgramDefinitionsAction,  CmList<StudyProgramModel>> {

    
    Logger logger = Logger.getLogger(GetProgramDefinitionsCommand.class);

    @Override
    public CmList<StudyProgramModel> execute(final Connection conn, GetProgramDefinitionsAction action) throws Exception {
        CmList<StudyProgramModel> cml = new CmArrayList<StudyProgramModel>();
        CmAdminDao dao = CmAdminDao.getInstance();
        cml.addAll(dao.getProgramDefinitions(conn,action.getAdminId()));
        return cml;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetProgramDefinitionsAction.class;
    }
}

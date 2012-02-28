package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.shared.client.rpc.action.CustomProgramDefinitionAction;
import hotmath.gwt.shared.client.util.CmException;

import java.sql.Connection;

public class CustomProgramDefinitionCommand implements
        ActionHandler<CustomProgramDefinitionAction, CmList<CustomProgramModel>> {

    @Override
    public CmList<CustomProgramModel> execute(Connection conn, CustomProgramDefinitionAction action) throws Exception {
        switch (action.getAction()) {
            case GET:
                return CmCustomProgramDao.getInstance().getCustomPrograms(conn, action.getAdminId());
    
            case DELETE:
                CmCustomProgramDao.getInstance().deleteCustomProgram(conn, action.getProgramId());
                return null;
    
            case ARCHIVE:
            	CustomProgramModel model = CmCustomProgramDao.getInstance().archiveCustomProgram(action.getProgramId());
            	CmList<CustomProgramModel> list = new CmArrayList<CustomProgramModel>();
            	list.add(model);
            	return list;
    
            default:
                throw new CmException("Unknown ActionType: " + action);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CustomProgramDefinitionAction.class;
    }
}

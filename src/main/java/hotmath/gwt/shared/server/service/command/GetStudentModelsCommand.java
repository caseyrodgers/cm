package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelsAction;

import java.sql.Connection;
import java.util.List;

/**
 * 
 * @author bob
 *
 */

public class GetStudentModelsCommand implements ActionHandler<GetStudentModelsAction, CmList<StudentModelI>> {

    @Override
    public CmList<StudentModelI> execute(Connection conn, GetStudentModelsAction action) throws Exception {
        CmStudentDao dao = CmStudentDao.getInstance();
        List<StudentModelI> l = dao.getStudentModelBase(conn, action.getUids());
        CmList<StudentModelI> cmList = new CmArrayList<StudentModelI>();
        for (StudentModelI sm : l) {
        	cmList.add(sm);
        }
        return cmList;
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetStudentModelsAction.class;
    }

}

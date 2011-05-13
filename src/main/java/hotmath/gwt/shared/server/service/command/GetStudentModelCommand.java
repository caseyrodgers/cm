package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;

import java.sql.Connection;

public class GetStudentModelCommand implements ActionHandler<GetStudentModelAction, StudentModelI> {

    @Override
    public StudentModelI execute(Connection conn, GetStudentModelAction action) throws Exception {
        CmStudentDao dao = CmStudentDao.getInstance();
        return dao.getStudentModelBase(conn, action.getUid());
    }

    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetStudentModelAction.class;
    }

}

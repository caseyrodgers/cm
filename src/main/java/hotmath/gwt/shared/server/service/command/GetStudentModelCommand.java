package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;

public class GetStudentModelCommand implements ActionHandler<GetStudentModelAction, StudentModelI> {

    @Override
    public StudentModelI execute(Connection conn, GetStudentModelAction action) throws Exception {
        CmStudentDao dao = new CmStudentDao();
        return dao.getStudentModel(action.getUid());
    }

    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetStudentModelAction.class;
    }

}

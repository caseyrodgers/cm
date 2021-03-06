package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StringHolder;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.UnregisterStudentsAction;

import java.sql.Connection;

public class UnregisterStudentsCommand implements ActionHandler<UnregisterStudentsAction, StringHolder> {

    @Override
    public StringHolder execute(Connection conn, UnregisterStudentsAction action) throws Exception {
        CmStudentDao dao = CmStudentDao.getInstance();
        return dao.unregisterStudents(conn, action.getStudentList());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return UnregisterStudentsAction.class;
    }
}

package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.rpc.action.UnregisterStudentsAction;

import java.sql.Connection;

public class UnregisterStudentsCommand implements ActionHandler<UnregisterStudentsAction, StringHolder> {

    @Override
    public StringHolder execute(Connection conn, UnregisterStudentsAction action) throws Exception {
        CmStudentDao dao = new CmStudentDao();
        return dao.unregisterStudents(conn, action.getStudentList());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return UnregisterStudentsAction.class;
    }
}

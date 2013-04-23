package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.AssignmentLessonData;
import hotmath.gwt.cm_rpc.client.rpc.AssignmentGetLessonsAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;

public class AssignmentGetLessonsCommand implements ActionHandler<AssignmentGetLessonsAction, AssignmentLessonData>{

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return null;
    }

    @Override
    public AssignmentLessonData execute(Connection conn, AssignmentGetLessonsAction action) throws Exception {
        return AssignmentDao.getInstance().getAssignmentLessonData();
    }

}

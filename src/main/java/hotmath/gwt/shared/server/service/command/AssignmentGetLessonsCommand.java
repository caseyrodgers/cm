package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.AssignmentLessonData;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.AssignmentGetLessonsAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

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

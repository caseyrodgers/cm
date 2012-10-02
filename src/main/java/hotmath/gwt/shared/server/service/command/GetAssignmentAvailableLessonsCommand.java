package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.assignment.LessonDto;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentAvailableLessonsAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

public class GetAssignmentAvailableLessonsCommand implements ActionHandler<GetAssignmentAvailableLessonsAction, CmList<LessonDto>> {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentAvailableLessonsAction.class;
    }

    @Override
    public CmList<LessonDto> execute(Connection conn, GetAssignmentAvailableLessonsAction action) throws Exception {
        CmList<LessonDto> lessons = new CmArrayList<LessonDto>();
        lessons.addAll(AssignmentDao.getInstance().getAvailableLessons());
        return lessons;
    }

}

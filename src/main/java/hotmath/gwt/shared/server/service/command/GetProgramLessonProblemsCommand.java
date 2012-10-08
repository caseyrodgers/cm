package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonProblemsAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;
import java.util.List;

/** Provide high level access to program listing data
 * 
 * @author casey
 *
 */
public class GetProgramLessonProblemsCommand implements ActionHandler<GetProgramLessonProblemsAction, CmList<ProblemDto>>{

    @Override
    public CmList<ProblemDto> execute(final Connection conn, GetProgramLessonProblemsAction action) throws Exception {
        List<ProblemDto> problems = AssignmentDao.getInstance().getLessonProblemsFor(conn,action.getLesson(),action.getFile(), action.getSubject());
        CmList<ProblemDto> data = new CmArrayList<ProblemDto>();
        data.addAll(problems);
        return data;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetProgramLessonProblemsAction.class;
    }
}

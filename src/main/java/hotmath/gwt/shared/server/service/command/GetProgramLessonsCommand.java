package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmProgramListingDao;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonsAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;

import java.sql.Connection;

/** Provide high level access to program listing data
 * 
 * @author casey
 *
 */
public class GetProgramLessonsCommand implements ActionHandler<GetProgramLessonsAction, CmList<ProgramLesson>>{

    @Override
    public CmList<ProgramLesson> execute(final Connection conn, GetProgramLessonsAction action) throws Exception {
        CmList<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(conn, action.getTestDefId(), action.getSegment(), action.getChapter());
        return lessons;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetProgramLessonsAction.class;
    }
}

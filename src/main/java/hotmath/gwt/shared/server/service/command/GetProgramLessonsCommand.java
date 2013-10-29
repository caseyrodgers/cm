package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmProgramListingDao;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonsAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;

import java.sql.Connection;

/** Provide high level access to program listing data
 * 
 * @author casey
 *
 */
public class GetProgramLessonsCommand implements ActionHandler<GetProgramLessonsAction, CmList<ProgramLesson>>, ActionHandlerManualConnectionManagement{

    @Override
    public CmList<ProgramLesson> execute(final Connection conn, GetProgramLessonsAction action) throws Exception {
        CmList<ProgramLesson> lessons =
        	new CmProgramListingDao().getLessonsFor(action.getTestDefId(), action.getSegment(), action.getChapter(), action.getSectionCount());
        return lessons;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetProgramLessonsAction.class;
    }
}

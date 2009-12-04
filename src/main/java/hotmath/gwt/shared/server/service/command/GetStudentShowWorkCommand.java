package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetStudentShowWorkAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;

/** Return list of Student whiteboard entries.  Should be a single
 *  entry for each unique whiteboard created for a given run_id.
 *  
 * @author casey
 *
 */
public class GetStudentShowWorkCommand implements ActionHandler<GetStudentShowWorkAction, CmList<StudentShowWorkModel>>{

    @Override
    public CmList<StudentShowWorkModel> execute(final Connection conn, GetStudentShowWorkAction action) throws Exception {
        return new CmStudentDao().getStudentShowWork(conn, action.getUid(), action.getRunId());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetStudentShowWorkAction.class;
    }
}

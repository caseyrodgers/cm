package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StudentShowWorkModelPojo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetStudentShowWorkAction;

import java.sql.Connection;

/** Return list of Student whiteboard entries.  Should be a single
 *  entry for each unique whiteboard created for a given run_id.
 *  
 * @author casey
 *
 */
public class GetStudentShowWorkCommand implements ActionHandler<GetStudentShowWorkAction, CmList<StudentShowWorkModelPojo>>{

    @Override
    public CmList<StudentShowWorkModelPojo> execute(final Connection conn, GetStudentShowWorkAction action) throws Exception {
        return CmStudentDao.getInstance().getStudentShowWork(conn, action.getUid(), action.getRunId());
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetStudentShowWorkAction.class;
    }
}

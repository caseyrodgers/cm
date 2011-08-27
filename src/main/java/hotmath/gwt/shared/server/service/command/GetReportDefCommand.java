package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc.server.service.ActionHandlerManualConnectionManagement;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.rpc.action.GetReportDefAction;

/**
 * Get the report def key for the specified <code>List<Integer></code> of student UIDs
 *
 * @author bob
 *
 */

public class GetReportDefCommand implements ActionHandlerManualConnectionManagement, ActionHandler<GetReportDefAction, StringHolder> {

	public StringHolder execute(Connection conn, GetReportDefAction action) throws Exception { 
        CmAdminDao dao = CmAdminDao.getInstance();
        return new StringHolder(dao.getPrintableStudentReportId(action.getStudentUids()));
    }

    public Class<? extends Action<? extends Response>> getActionType() {
        return GetReportDefAction.class;
    }

}

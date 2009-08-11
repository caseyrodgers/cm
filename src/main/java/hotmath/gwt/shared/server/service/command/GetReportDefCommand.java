package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetReportDefAction;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.gwt.shared.server.service.ActionHandlerManualConnectionManagement;

/**
 * Get the report def key for the specified <code>List<Integer></code> of student UIDs
 *
 * @author rfhall
 *
 */

public class GetReportDefCommand implements ActionHandlerManualConnectionManagement, ActionHandler<GetReportDefAction, StringHolder> {

	public StringHolder execute(Connection conn, GetReportDefAction action) throws Exception { 
        CmAdminDao dao = new CmAdminDao();
        return new StringHolder(dao.getPrintableStudentReportId(action.getStudentUids()));
    }

    public Class<? extends Action<? extends Response>> getActionType() {
        return GetReportDefAction.class;
    }

}

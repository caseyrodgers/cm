package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_admin.client.model.GroupCopyModel;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetGroupAssignmentsAction;

import java.sql.Connection;

import org.apache.log4j.Logger;

/** Get Student Active Info via CmProgramFlow
 *  
 * @author bob
 *
 */
public class GetGroupAssignmentsCommand implements ActionHandler<GetGroupAssignmentsAction, CmList<GroupCopyModel>> {

    static final Logger logger = Logger.getLogger(GetActiveInfoForStudentUidCommand.class);

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetGroupAssignmentsAction.class;
    }

    @Override
    public CmList<GroupCopyModel> execute(Connection conn, GetGroupAssignmentsAction action) throws Exception {
        CmList<GroupCopyModel> assList = new CmArrayList<GroupCopyModel>();
        assList.addAll(AssignmentDao.getInstance().getGroupAssignments(action.getAdminId()));
        return assList;
    }

}






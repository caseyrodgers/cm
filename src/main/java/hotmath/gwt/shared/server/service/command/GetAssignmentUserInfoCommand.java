package hotmath.gwt.shared.server.service.command;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.CmMobileAssignmentUser;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetAssignmentUserInfoAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.HaUser;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;
import java.util.List;

public class GetAssignmentUserInfoCommand implements ActionHandler<GetAssignmentUserInfoAction, CmMobileAssignmentUser> {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentUserInfoAction.class;
    }

    @Override
    public CmMobileAssignmentUser execute(Connection conn, GetAssignmentUserInfoAction action) throws Exception {
        
        HaUser userInfo = HaUserDao.getInstance().lookUser(action.getUid(),  false);
        List<StudentAssignmentInfo> assignments = AssignmentDao.getInstance().getAssignmentsForUser(action.getUid());

        AssignmentUserInfo assignmentUserInfo = AssignmentDao.getInstance().getStudentAssignmentMetaInfo(action.getUid());
        
        return new CmMobileAssignmentUser(action.getUid(),userInfo.getUserName(), assignmentUserInfo, assignments); 
    }

}

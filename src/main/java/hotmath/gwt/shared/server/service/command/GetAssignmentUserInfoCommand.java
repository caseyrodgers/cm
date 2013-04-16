package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;
import java.util.List;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.gwt.cm_mobile_assignments.client.user.CmMobileAssignmentUser;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentUserInfoAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.testset.ha.HaUser;
import hotmath.testset.ha.HaUserDao;

public class GetAssignmentUserInfoCommand implements ActionHandler<GetAssignmentUserInfoAction, CmMobileAssignmentUser> {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetAssignmentUserInfoAction.class;
    }

    @Override
    public CmMobileAssignmentUser execute(Connection conn, GetAssignmentUserInfoAction action) throws Exception {
        
        HaUser userInfo = HaUserDao.getInstance().lookUser(action.getUid(),  false);
        List<StudentAssignmentInfo> assignments = AssignmentDao.getInstance().getAssignmentsForUser(action.getUid());
        
        return new CmMobileAssignmentUser(action.getUid(), userInfo.getUserName(),assignments); 
    }

}

package hotmath.gwt.cm_mobile3.server.rpc;

import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_mobile3.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaBasicUser.UserType;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.server.service.command.GetUserInfoCommand;
import hotmath.testset.ha.HaUserFactory;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class GetCmMobileLoginCommand implements ActionHandler<GetCmMobileLoginAction, CmMobileUser> {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCmMobileLoginAction.class;
    }

    @Override
    public CmMobileUser execute(Connection conn, GetCmMobileLoginAction action) throws Exception {

        HaBasicUser basicUser = HaUserFactory.loginToCatchup(conn, action.getName(), action.getPassword());
        if (basicUser.getUserType() != UserType.STUDENT)
            throw new CmException("Invalid user type: " + basicUser.getUserType());

        CmProgramFlow programFlow = new CmProgramFlow(conn, basicUser.getUserKey());
        
        StudentModelI sm = CmStudentDao.getInstance().getStudentModelBase(conn, basicUser.getUserKey());
        StudentActiveInfo active = programFlow.getActiveInfo();
        CmMobileUser mobileUser = new CmMobileUser(sm.getUid(), active.getActiveTestId(), active.getActiveSegment(), active.getActiveSegmentSlot(), active.getActiveRunId());

        /**
         * get list of previous prescribed lessons?
         * 
         */
        PreparedStatement ps = null;
        try {
            UserLoginResponse userLoginResponse = new GetUserInfoCommand().execute(conn, new GetUserInfoAction(mobileUser.getUserId(), null));
            mobileUser.setBaseLoginResponse(userLoginResponse);
            
            CmProgramFlowAction nextAction = programFlow.getActiveFlowAction(conn);
            mobileUser.setFlowAction(nextAction);
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        return mobileUser;
    }

}

package hotmath.gwt.cm_mobile3.server.rpc;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_mobile3.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaBasicUser.UserType;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.server.service.command.GetUserInfoCommand;
import hotmath.gwt.shared.server.service.command.GetUserInfoCommand.CustomProgramInfo;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRun;
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

       int uid = action.getUid();
       
       HaBasicUser basicUser=null;
       if(uid > 0) {
           basicUser = HaUserFactory.getLoginUserInfo(conn, uid, "STUDENT");
       }
       else if(action.getName().equals("catchup_demo") && action.getPassword().equals("demo")) {
           basicUser = HaUserFactory.createDemoUser(conn);
        }
       else {
           basicUser = HaUserFactory.loginToCatchup(conn, action.getName(), action.getPassword());
       }
       
       if (basicUser.getUserType() != UserType.STUDENT)
            throw new CmException("Invalid user type: " + basicUser.getUserType());
        
        

        CmProgramFlow programFlow = new CmProgramFlow(conn, basicUser.getUserKey());
        
        StudentModelI sm = CmStudentDao.getInstance().getStudentModelBase(conn, basicUser.getUserKey());
        StudentActiveInfo active = programFlow.getActiveInfo();
        CmMobileUser mobileUser = new CmMobileUser(sm.getUid(), active.getActiveTestId(), active.getActiveSegment(), active.getActiveSegmentSlot(), active.getActiveRunId());

        
        /** create new security key for this login sesssion */
        String securityKey="";
        if(uid == 0) {
            securityKey = HaLoginInfoDao.getInstance().addLoginInfo(conn, basicUser, new ClientEnvironment(false),true);
        }
        
        int programSegmentCount = 0;
        String testTitle = "";
        boolean isCustomProgram = programFlow.getUserProgram().getTestDefId() == CmProgram.CUSTOM_PROGRAM.getDefId();
        if(isCustomProgram) {
            CustomProgramInfo cpi = GetUserInfoCommand.processCustomProgram(conn, sm.getUid(), active, programFlow.getUserProgram());
            programSegmentCount = cpi.getProgramSegmentCount();
            testTitle = cpi.getTitle();
            
            // TODO:
            // here the CP is now ready for use, but what about the 
            // programSegmentCount and testTitle ...?
        }
                
        
        
        
        

        /**
         * get list of previous prescribed lessons
         * 
         */
        PreparedStatement ps = null;
        try {
            GetUserInfoAction loginAction = new GetUserInfoAction(mobileUser.getUserId(), null);
            UserLoginResponse userLoginResponse = new GetUserInfoCommand().execute(conn, loginAction);
            mobileUser.setBaseLoginResponse(userLoginResponse);
            mobileUser.setSecurityKey(securityKey);
            
            CmProgramFlowAction nextAction = programFlow.getActiveFlowAction(conn);
            
            mobileUser.setFlowAction(nextAction);
            
            /** check if non flash enabled prescription, and mark next action
             *  should be to NOT show it.  Instead client should handle the
             *  flash required error.
             *   
             */
            if(nextAction.getPrescriptionResponse() != null) {
                if(nextAction.getPrescriptionResponse().getPrescriptionData().getCurrSession().dependsOnFlash()) {
                    mobileUser.setFlowAction(new CmProgramFlowAction(CmPlace.ERROR_FLASH_REQUIRED));
                }
            }
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        
        return mobileUser;
    }

}

package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;
import java.util.UUID;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_core.client.model.SearchAllowMode;
import hotmath.gwt.cm_rpc.client.rpc.NewMobileUserAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.shared.client.CmProgram;
import hotmath.testset.ha.HaUserFactory;

public class NewMobileUserCommand implements ActionHandler<NewMobileUserAction, RpcData> {

	@Override
	public Class<? extends Action<? extends Response>> getActionType() {
		return NewMobileUserAction.class;
	}

	@Override
	public RpcData execute(Connection conn, NewMobileUserAction action) throws Exception {
		
		// mobile user admin account
		HaBasicUser mobileAdmin = HaUserFactory.loginToCatchup(conn, "cm_mobile", "cm_mobile");
		
		String newUserName = action.getDeviceId();
		String newPwd = action.getDeviceId();
		
        int newUid = HaUserFactory.createUser(conn, mobileAdmin.getUserKey(),"none", newUserName, newPwd);
                
        CmStudentDao.getInstance().assignProgramToStudent(conn, newUid,CmProgram.AUTO_ENROLL,null);
        CmStudentDao.getInstance().updateStudentSettings(conn, newUid, false, false, false, true, 70, true, false, false, SearchAllowMode.ENABLED_EXCEPT_TESTS);
        
        return new RpcData("uid=" + newUid);
	}

}

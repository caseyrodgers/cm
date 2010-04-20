package hotmath.gwt.cm_mobile.server.rpc;

import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaBasicUser.UserType;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.testset.ha.HaUserFactory;

import java.sql.Connection;

public class GetCmMobileLoginCommand implements ActionHandler<GetCmMobileLoginAction, CmMobileUser> {

    @Override
    public CmMobileUser execute(Connection conn, GetCmMobileLoginAction action) throws Exception {
        
        HaBasicUser basicUser = HaUserFactory.loginToCatchup(action.getName(),action.getPassword());
        if(basicUser.getUserType() != UserType.STUDENT)
            throw new CmException("Invalid user type: " + basicUser.getUserType());
        
        
        CmMobileUser mobileUser = new CmMobileUser();
        mobileUser.setUserId(basicUser.getUserKey());
        return mobileUser;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCmMobileLoginAction.class;
    }

}

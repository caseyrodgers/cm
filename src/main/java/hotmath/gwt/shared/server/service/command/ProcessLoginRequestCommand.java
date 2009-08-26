package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.ProcessLoginRequestAction;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.UserInfo;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.HaLoginInfo;

import java.sql.Connection;

public class ProcessLoginRequestCommand implements ActionHandler<ProcessLoginRequestAction, UserInfo>{
    
    @Override
    public UserInfo execute(Connection conn, ProcessLoginRequestAction action) throws Exception {
        // TODO Auto-generated method stub
        HaLoginInfo haLoginInfo = HaLoginInfo.getLoginInfo(conn, action.getKey());
        if(haLoginInfo.getIsConsumed()) {
            throw new CmException("Security key has already been consumed: " + action.getKey());
        }
        
        UserInfo userInfo = new UserInfo();
        userInfo.setUid(haLoginInfo.getUserId());
        return userInfo;
    }

    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return ProcessLoginRequestAction.class;
    }
}

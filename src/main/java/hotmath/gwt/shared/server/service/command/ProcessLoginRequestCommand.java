package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.data.HaLoginInfo;
import hotmath.gwt.shared.client.rpc.action.ProcessLoginRequestAction;

import java.sql.Connection;

public class ProcessLoginRequestCommand implements ActionHandler<ProcessLoginRequestAction, UserInfo>{
    
    @Override
    public UserInfo execute(Connection conn, ProcessLoginRequestAction action) throws Exception {
            HaLoginInfo haLoginInfo = HaLoginInfoDao.getInstance().getLoginInfo(conn, action.getKey());
            UserInfo userInfo = new UserInfo();
            userInfo.setUid(haLoginInfo.getUserId());
            userInfo.setLoginName(haLoginInfo.getLoginName());
            return userInfo;
    }

    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return ProcessLoginRequestAction.class;
    }
}

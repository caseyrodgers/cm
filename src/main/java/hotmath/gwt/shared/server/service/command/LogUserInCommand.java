package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.login.ClientEnvironment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.LogUserInAction;
import hotmath.testset.ha.HaUser;

import java.sql.Connection;

/** Log user into CM and return login key
 * 
 * rdata == key (the login key), message (any error message)
 * 
 * @author casey
 *
 */
public class LogUserInCommand implements ActionHandler<LogUserInAction, RpcData>{

    
    public RpcData execute(Connection conn, LogUserInAction action) throws Exception {

        RpcData rdata = new RpcData();
        try {
            HaUser huser = HaUser.lookupUserByPasscode(conn, action.getPassword());
            rdata.putData("key", HaLoginInfoDao.getInstance().addLoginInfo(conn, huser, new ClientEnvironment(action.getBrowserInfo()),true));
        }
        catch(Exception e) {
            rdata.putData("message", e.getMessage());
        }
        
        return rdata;
    }

    public Class<? extends Action<? extends Response>> getActionType() {
        return LogUserInAction.class;
    }

}

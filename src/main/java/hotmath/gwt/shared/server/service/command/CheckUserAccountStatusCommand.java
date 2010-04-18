package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CheckUserAccountStatusAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;







/** Provide basic, general access to status of user accounts
 * 
 * Check for unique password.
 * 
 * @author casey
 *
 */
public class CheckUserAccountStatusCommand implements ActionHandler<CheckUserAccountStatusAction, RpcData>{

    public RpcData execute(Connection conn, CheckUserAccountStatusAction action) throws Exception {
        
        RpcData rdata = new RpcData();
        
        PreparedStatement stmt=null;
        try {
            String sql = "select 'x' from HA_USER where user_passcode = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, action.getPassword());
            
            ResultSet rs = stmt.executeQuery();
            rdata.putData("message","password is " + (rs.first()?"duplicate":"unique"));
            
            
            return rdata;
        }
        finally {
            SqlUtilities.releaseResources(null,stmt,null);
        }
    }

    
    public Class<? extends Action<? extends Response>> getActionType() {
           return CheckUserAccountStatusAction.class;
    }

}

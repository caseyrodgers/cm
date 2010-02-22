package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.LogRetryActionFailedAction;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

public class LogRetryActionFailedCommand implements ActionHandler<LogRetryActionFailedAction, RpcData>{
    
    Logger logger = Logger.getLogger(LogRetryActionFailedCommand.class);
    
    @Override
    public RpcData execute(Connection conn, LogRetryActionFailedAction action) throws Exception {
        PreparedStatement pstat=null;
        try {
            String sql = "insert into HA_ACTION_FAIL_LOG(uid, class_name, action_info,fail_date)values(?,?,?,now())";
            pstat = conn.prepareStatement(sql);
            
            pstat.setInt(1, action.getUid());
            pstat.setString(2, action.getClassName());
            pstat.setString(3, getActionInfo(action.getAction()));
            
            if(pstat.executeUpdate()!= 1)
                logger.info("Could not save failed action request: " + action);
        }
        finally {
            SqlUtilities.releaseResources(null,pstat,null);
        }
        return new RpcData("status=OK");
    }
    
    private String getActionInfo(Action<? extends Response> a) {
        return a==null?"":a.toString();
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return LogRetryActionFailedAction.class;
    }

}

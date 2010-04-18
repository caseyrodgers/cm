package hotmath.gwt.shared.server.service.command;

import java.sql.Connection;
import java.sql.PreparedStatement;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.shared.client.rpc.action.SaveCmLoggerTextAction;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.util.sql.SqlUtilities;

public class SaveCmLoggerTextCommand implements ActionHandler<SaveCmLoggerTextAction, RpcData>{

    @Override
    public RpcData execute(Connection conn, SaveCmLoggerTextAction action) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = "insert into HA_DEBUG_LOG(uid, log_messages,save_date)values(?,?,now())";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, action.getUid());
            ps.setString(2, action.getLogMsgs());
            if(ps.executeUpdate() != 1)
                throw new Exception("Could not save log messages for '" + action.getUid() + "'");
            
            return new RpcData("status=OK");
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return SaveCmLoggerTextAction.class;
    }

}

package hotmath.gwt.shared.server.service.command;


import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.rpc.GetCatchupMathDebugAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;
import java.sql.ResultSet;

/** A debugging message 
 * 
 * @author casey
 *
 */
public class GetCatchupMathDebugCommand implements ActionHandler<GetCatchupMathDebugAction, RpcData> {
    @Override
    public RpcData execute(Connection conn, GetCatchupMathDebugAction action) throws Exception {
        RpcData data = new RpcData();
        RData rdata = getRData(conn);
        data.putData("rid", rdata.rid);
        data.putData("pid", rdata.pid);
        data.putData("uid",  rdata.userId);
        return data;
    }

    private RData getRData(final Connection conn) throws Exception {
        
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("DEBUG_GET_WHITEBOARD_TEST");
        ResultSet rs = conn.createStatement().executeQuery(sql);
        if(!rs.next()) {
            throw new Exception("No more test ids");
        }
        
        RData r = new RData();
        int whiteboardId = rs.getInt("whiteboard_id");
        r.rid = rs.getInt("run_id");
        r.pid = rs.getString("pid");
        r.userId = rs.getInt("user_id");

        int cnt = conn.createStatement().executeUpdate("update junk set is_tested = 1 where whiteboard_id = " + whiteboardId);
        if(cnt != 1) {
            throw new Exception("Could not mark test as complete: " + whiteboardId);
        }
        
        return r;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCatchupMathDebugAction.class;
    }
    
    class RData {
        int rid,userId;
        String pid;
    }
}

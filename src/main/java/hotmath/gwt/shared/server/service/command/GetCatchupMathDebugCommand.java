package hotmath.gwt.shared.server.service.command;


import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.rpc.GetCatchupMathDebugAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.SetInmhItemAsViewedAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



/** A debugging message 
 * 
 * @author casey
 *
 */
public class GetCatchupMathDebugCommand implements ActionHandler<GetCatchupMathDebugAction, RpcData> {
    @Override
    public RpcData execute(Connection conn, GetCatchupMathDebugAction action) throws Exception {
        
        switch(action.getAction()) {
        case GET_NEXT:
            doGetNext(conn, action);
            
        case SETUP_TRANSISTION_TEST:
            setupTransitionTest(conn, action.getRunId());
        }
        
        return new RpcData("status=OK");
    }

    private void setupTransitionTest(Connection conn,int runId) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = 
                    "update HA_TEST_RUN_LESSON " +
                    "set date_completed = now() " +
                    "where run_id = ? " +
                    "and lesson_number > 0 ";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,  runId);
            
            int cnt=ps.executeUpdate();
            if(cnt == 0) {
                throw new Exception("No lessons in test_run marked as complete");
            }
            
            
            
            sql = 
                    "select p.pid " +
                    "from HA_TEST_RUN_LESSON_PID p " +
                    " JOIN HA_TEST_RUN_LESSON l on l.id = p.lid " +
                    " where l.lesson_number = 0 " + 
                    " and l.run_id = ? ";

            ps.close();
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1,  runId);
            
            ResultSet rs = ps.executeQuery();
            
            // skip first
            rs.first();
            
            while(rs.next()) {
                String pid = rs.getString(1);
                SetInmhItemAsViewedAction action = new SetInmhItemAsViewedAction(runId, CmResourceType.PRACTICE, pid, 0);
                new SetInmhItemAsViewedCommand().execute(conn,  action);
            }
        }
        finally {
            SqlUtilities.releaseResources(null,  ps,  null);
        }
        
    }

    private RpcData doGetNext(Connection conn, GetCatchupMathDebugAction action) throws Exception {
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

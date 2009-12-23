package hotmath.gwt.cm_tools.server.service;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.SetInmhItemAsViewedAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/** Set named INMH resource as being viewed in given runid
 * 
 * @TODO:  really do not need to return anything, how to specific generic with void
 * 
 * @author casey
 *
 */
public class SetInmhItemAsViewedCommand implements ActionHandler<SetInmhItemAsViewedAction, RpcData> {

    @Override
    public RpcData execute(final Connection conn, SetInmhItemAsViewedAction action) throws Exception {
        PreparedStatement pstat = null;
        try {
            String sql = "insert into HA_TEST_RUN_INMH_USE(run_id, item_type, item_file, view_time, session_number)values(?,?,?,?,?)";
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, action.getRunId());
            pstat.setString(2, action.getType());
            pstat.setString(3, action.getFile());
            pstat.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstat.setInt(5, new HaTestRunDao().lookupTestRun(conn, action.getRunId()).getHaTest().getUser().getActiveTestRunSession());

            int cnt = pstat.executeUpdate();
            if (cnt != 1)
                throw new Exception("Error adding test run item view");

            RpcData rpcData = new RpcData();
            rpcData.putData("success", "true");
            
            return rpcData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException("Error adding test run item view: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SetInmhItemAsViewedAction.class;
    }

}

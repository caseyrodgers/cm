package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.SetInmhItemAsViewedAction;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

/** Set named INMH resource as being viewed in given runid
 * 
 * @TODO:  really do not need to return anything, how to specific generic with void
 * 
 * @author casey
 *
 */
public class SetInmhItemAsViewedCommand implements ActionHandler<SetInmhItemAsViewedAction, RpcData> {

	private static Logger logger = Logger.getLogger(SetInmhItemAsViewedCommand.class);

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
            pstat.setInt(5, action.getSessionNumber());

            int cnt = pstat.executeUpdate();
            if (cnt != 1)
                throw new Exception("Error adding test run item view");

            RpcData rpcData = new RpcData();
            rpcData.putData("success", "true");
            
            return rpcData;
        } catch (Exception e) {
        	logger.error(String.format("*** Error executing Action: %s", action.toString()), e);
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

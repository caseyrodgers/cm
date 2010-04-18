package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetViewedInmhItemsAction;
import hotmath.gwt.shared.client.rpc.result.GetViewedInmhItemsResult;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/** Get list of all INMH items that were viewed during 
 *  this run
 *  
 * @author casey
 *
 */
public class GetViewedInmhItemsCommand implements ActionHandler<GetViewedInmhItemsAction, GetViewedInmhItemsResult>{
    

    @Override
    public GetViewedInmhItemsResult execute(final Connection conn, GetViewedInmhItemsAction action) throws Exception {
        
        int runId = action.getRunId();
    
        ArrayList<RpcData> data = new ArrayList<RpcData>();
        try {
            PreparedStatement pstat = null;
            try {
                String sql = "select * from HA_TEST_RUN_INMH_USE where run_id = ?";
                pstat = conn.prepareStatement(sql);

                pstat.setInt(1, runId);

                ResultSet rs = pstat.executeQuery();
                while (rs.next()) {
                    String type = rs.getString("item_type");
                    String file = rs.getString("item_file");

                    RpcData rpcData = new RpcData();
                    rpcData.putData("type", type);
                    rpcData.putData("file", file);
                    data.add(rpcData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new CmRpcException("Error adding test run item view: " + e.getMessage());
            } finally {
                SqlUtilities.releaseResources(null, pstat, null);
            }

        } catch (Exception e) {
            throw new CmRpcException(e);
        }
        
        GetViewedInmhItemsResult result = new GetViewedInmhItemsResult(data);
        return result;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetViewedInmhItemsAction.class;
    }

}

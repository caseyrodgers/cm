package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.ClearWhiteboardDataAction;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

/** Clear all whiteboard command data for this user
 *  for the named pid
 * 
 *  Return RpcData with single var (status == OK)
 *  
 * @author casey
 *
 */
public class ClearWhiteboardDataCommand implements ActionHandler<ClearWhiteboardDataAction, RpcData> {

    static Logger logger = Logger.getLogger(SaveWhiteboardDataCommand.class);
    
    @Override
    public RpcData execute(Connection conn, ClearWhiteboardDataAction action) throws Exception {

        PreparedStatement pstat = null;
        try {
            String sql = "delete from HA_TEST_RUN_WHITEBOARD where user_id = ? and pid = ? and run_id = ?";
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, action.getUid());
            pstat.setString(2, action.getPid());
            pstat.setInt(3, action.getRid());
            
            int cntDel = pstat.executeUpdate();
            
            RpcData rData = new RpcData();
            rData.putData("status", "OK");
            rData.putData("deleted", cntDel);
            
            return rData;
            
        } catch (Exception e) {
            throw new CmRpcException(e);
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }        
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ClearWhiteboardDataAction.class;
    }

}

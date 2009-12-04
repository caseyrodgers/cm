package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.ClearWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.action.SaveWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.action.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

/** Save a single whiteboard command into database
 * 
 *  Return RpcData with single var (status == OK)
 *  
 * @author casey
 *
 */
public class SaveWhiteboardDataCommand implements ActionHandler<SaveWhiteboardDataAction, RpcData> {

    static Logger logger = Logger.getLogger(SaveWhiteboardDataCommand.class);
    
    @Override
    public RpcData execute(Connection conn, SaveWhiteboardDataAction action) throws Exception {
        RpcData rData = new RpcData();
        
        if(action.getCommandType() == CommandType.CLEAR) {
            rData = new ClearWhiteboardDataCommand().execute(conn,new ClearWhiteboardDataAction(action.getUid(), action.getRid(),action.getPid()));
        }
        else {
            PreparedStatement pstat = null;
            try {
                String sql = "insert into HA_TEST_RUN_WHITEBOARD(user_id, pid, command, command_data, insert_time_mills, run_id) "
                        + " values(?,?,?,?,?,?) ";
                pstat = conn.prepareStatement(sql);
    
                pstat.setInt(1, action.getUid());
                pstat.setString(2, action.getPid());
                pstat.setString(3, "draw");
                pstat.setString(4, action.getCommandData());
                pstat.setLong(5, System.currentTimeMillis());
                
                if(action.getRid() == 0)
                    logger.warn("run_id is null ... should never happen!");
                
                pstat.setInt(6, action.getRid());
    
                if (pstat.executeUpdate() != 1)
                    throw new Exception("Could not save whiteboard data (why?)");
                
                rData.putData("status", "OK");
            } catch (Exception e) {
                throw new CmRpcException(e);
            } finally {
                SqlUtilities.releaseResources(null, pstat, null);
            }
        }
        
        return rData;
    }
    
    private void clearWhiteBoard(final Connection conn) throws Exception {

    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveWhiteboardDataAction.class;
    }

}

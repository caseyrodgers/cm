package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.CompressHelper;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Get all whiteboard data for given user/pid
 * 
 * @author casey
 *
 */
public class GetWhiteboardDataCommand implements ActionHandler<GetWhiteboardDataAction,CmList<WhiteboardCommand>>{

    @Override
    public CmList<WhiteboardCommand> execute(final Connection conn, GetWhiteboardDataAction action) throws Exception {

        CmList<WhiteboardCommand> data = new CmArrayList<WhiteboardCommand>();
        PreparedStatement pstat = null;
        try {
            
            pstat = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("GET_WHITEBOARD_SQL"));

            pstat.setInt(1,action.getUid());
            pstat.setString(2, action.getPid());
            pstat.setInt(3,action.getRunId());

            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                WhiteboardCommand rd = new WhiteboardCommand(rs.getString("command"), loadCommandData(rs), false);
                data.add(rd);
            }

            return data;
        } catch (Exception e) {
            throw new CmRpcException(e);
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }        
    }
    
    private String loadCommandData(ResultSet rs) throws Exception {
    	byte[] compressed = rs.getBytes("command_data");
    	if (compressed != null && compressed.length > 0 && compressed[0] != "{".getBytes("UTF-8")[0]) {
    		return CompressHelper.decompress(compressed);
    	}
    	else {
    		return rs.getString("command_data");
    	}
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetWhiteboardDataAction.class;
    }

}

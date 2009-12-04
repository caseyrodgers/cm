package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.result.WhiteboardCommand;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

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
            String sql = "select * from HA_TEST_RUN_WHITEBOARD "
                    + " where user_id = ? and pid = ? order by insert_time_mills";

            pstat = conn.prepareStatement(sql);

            pstat.setInt(1,action.getUid());
            pstat.setString(2, action.getPid());

            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                List<String> ln = new CmArrayList<String>();
                WhiteboardCommand rd = new WhiteboardCommand(rs.getString("command"), rs.getString("command_data"));
                data.add(rd);
            }

            return data;
        } catch (Exception e) {
            throw new CmRpcException(e);
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }        
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetWhiteboardDataAction.class;
    }

}

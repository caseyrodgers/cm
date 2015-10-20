package hotmath.gwt.shared.server.service.command;

import hotmath.cm.server.model.StudentEventsDao;
import hotmath.cm.server.model.StudentEventsDao.EventType;
import hotmath.gwt.cm_rpc.client.rpc.SendMessageToGroupAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

/**
   Queue up message to be sent (either poll/native events)
   
 */
public class SendMessageToGroupCommand implements ActionHandler<SendMessageToGroupAction, RpcData> {
	
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SendMessageToGroupAction.class;
    }

    @Override
    public RpcData execute(Connection conn, SendMessageToGroupAction action) throws Exception {
        PreparedStatement st=null;
        try {
            String sql = "select uid from HA_USER where is_active = 1 and group_id = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, action.getGroupId()) ;
            
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                StudentEventsDao.getInstance().addStudentEvent(rs.getInt("uid"), EventType.MESSAGE, action.getMessage());
            }
            
        }
        finally {
            SqlUtilities.releaseResources(null,st,null);
        }
        
        return new RpcData("status=ok");
    }
}

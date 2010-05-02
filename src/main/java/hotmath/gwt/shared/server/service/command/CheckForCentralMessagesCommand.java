package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CentralMessage;
import hotmath.gwt.shared.client.rpc.action.CheckForCentralMessagesAction;
import hotmath.gwt.shared.client.rpc.action.CheckForCentralMessagesAction.MessageActionType;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;


public class CheckForCentralMessagesCommand implements ActionHandler<CheckForCentralMessagesAction, CmList<CentralMessage>>{

    /** Handles two operations: 
     * 
     *   MessageActionType.GET_UNREAD_MESSAGE:  Reads unread messages and returns list to caller
     *   MessageActionType.MARK_AS_VIEWED: updates the messages as being viewed and returns null
     *   
     */
    @Override
    public CmList<CentralMessage> execute(Connection conn, CheckForCentralMessagesAction action) throws Exception {
        if(action.getActionType() == MessageActionType.GET_UNREAD_MESSAGE) {
            return getUnreadMessages(conn, action.getUid());
        }
        else if(action.getActionType() == MessageActionType.MARK_AS_VIEWED){
            markMessagesAsRetreived(conn, action.getUid(), action.getMessages());
            return null;
        }
        else {
            throw new CmException("Uknown action type: " + action);
        }
    }

    private void markMessagesAsRetreived(final Connection conn, int uid, List<CentralMessage> messages) throws Exception {
        for(CentralMessage m: messages) {
            markMessageAsRetreived(conn, uid, m);
        }
    }
    
    private void markMessageAsRetreived(final Connection conn, int uid, CentralMessage msg) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("MARK_USER_MESSAGE_AS_RETRIEVED");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);
            ps.setInt(2, msg.getId());
            if(ps.executeUpdate() != 1)
                throw new Exception("Could not not set message as read for " + uid + ": " + msg);
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }
    
    private CmList<CentralMessage> getUnreadMessages(final Connection conn, int uid) throws Exception {
        PreparedStatement ps=null;
        try {
        CmList<CentralMessage> messages = new CmArrayList<CentralMessage>();    
        /** get any messages this user has not already seen
         * 
         */
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_USER_MESSAGES");
        ps = conn.prepareStatement(sql);
        ps.setInt(1, uid);
        ResultSet rs = ps.executeQuery();
        
        while(rs.next()) {
            CentralMessage cMsg = new CentralMessage(rs.getInt("id"),rs.getString("message_type"), rs.getString("message_text"));
            messages.add(cMsg);
        }
        return messages;
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }
    
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CheckForCentralMessagesAction.class;
    }
}

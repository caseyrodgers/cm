package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.SaveFeedbackAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

public class SaveFeedbackCommand implements ActionHandler<SaveFeedbackAction, RpcData> {

    static Logger logger = Logger.getLogger(SaveFeedbackCommand.class);
    @Override
    public RpcData execute(SaveFeedbackAction action) throws Exception {

        logger.info("Saving feedback: " + action);
        Connection conn = null;
        PreparedStatement pstat = null;

        try {

            String sql = "insert into HA_FEEDBACK(entry_date, comment,comment_url,state_info)values(now(),?,?,?)";

            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, action.getComments());
            pstat.setString(2, action.getCommentsUrl());
            pstat.setString(3, action.getStateInfo());

            if (pstat.executeUpdate() != 1)
                throw new Exception("could not save feedback comments");
        } catch (Exception e) {
            logger.info(e);
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }    
        
        RpcData rpcData = new RpcData();
        rpcData.putData("success","true");
        
        return rpcData; 
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SaveFeedbackAction.class;
    }

}

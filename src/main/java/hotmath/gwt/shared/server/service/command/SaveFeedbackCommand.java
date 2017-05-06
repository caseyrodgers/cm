package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm.client.ui.context.CatchupMathProgramCorruptedDialog;
import hotmath.gwt.cm_rpc.client.rpc.SaveFeedbackAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.util.sql.SqlUtilities;
import sb.mail.SbMailManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import org.apache.log4j.Logger;

public class SaveFeedbackCommand implements ActionHandler<SaveFeedbackAction, RpcData> {

    static Logger logger = Logger.getLogger(SaveFeedbackCommand.class);
    @Override
    public RpcData execute(final Connection conn, SaveFeedbackAction action) throws Exception {

        logger.info("Saving feedback: " + action);
        PreparedStatement pstat = null;

        try {

            String sql = "insert into HA_FEEDBACK(entry_date, comment,comment_url,state_info)values(now(),?,?,?)";

            pstat = conn.prepareStatement(sql);
            pstat.setString(1, action.getComments());
            pstat.setString(2, action.getCommentsUrl());
            pstat.setString(3, action.getStateInfo());

            if (pstat.executeUpdate() != 1)
                throw new Exception("could not save feedback comments");
            
            
            try {
    			
            	String toEmailAddrs="feedback@catchupmath.com";
            	String serverId = CatchupMathProperties.getInstance().getCmInstallationId();
            	String subject = "Cm Mobile Feedback (serverId=" + serverId + "): " + new Date();
            	String message = action.getComments() + "\n" + action.getStateInfo();
            	
            	//String[] emailTo = {"casey@catchupmath.com"};
            	SbMailManager.getInstance().sendMessage(subject, message, toEmailAddrs, null, "text/plain");
            }
            catch(Exception e) {
            	logger.error("Error sending feedback email to admin", e);
            }
            
        } catch (Exception e) {
            logger.info(e);
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
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

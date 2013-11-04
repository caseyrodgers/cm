package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.WebLinkDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.PublicAvailable;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import sb.mail.SbMailManager;
import sb.template.SbTemplateManager;

public class DoWebLinksCrudOperationCommand implements ActionHandler<DoWebLinksCrudOperationAction, RpcData> {
    
    Logger logger = Logger.getLogger(DoWebLinksCrudOperationCommand.class);

    @Override
    public RpcData execute(Connection conn, DoWebLinksCrudOperationAction action) throws Exception {
        
        switch(action.getOperation()) {
            case IMPORT_FROM_PUBLIC:
                WebLinkDao.getInstance().importPublicWebLink(action.getAdminId(),action.getWebLink());
                return new RpcData("status=OK");
        
            case DELETE:
                deleteWebLink(conn, action.getWebLink());
                return new RpcData("status=OK");
                
                
            case UPDATE:
            case ADD:

                boolean sendSuggestEmail= (action.getWebLink().getPublicAvailability() == PublicAvailable.PUBLIC_SUGGESTED)?true:false;
                if(action.getWebLink().getLinkId() > 0) {
                    WebLinkModel webLinkModel = WebLinkDao.getInstance().getWebLink(action.getWebLink().getLinkId());
                    if(webLinkModel != null && webLinkModel.getPublicAvailability() == PublicAvailable.PUBLIC_SUGGESTED) {
                        // already been sent
                        sendSuggestEmail=false;
                    }
                    deleteWebLink(conn, action.getWebLink());
                }
                
                
                if(sendSuggestEmail) {
                    sendWebLinkSuggestionEmail(action.getWebLink());
                }
                
                WebLinkDao.getInstance().addWebLink(action.getWebLink());
                
                return new RpcData("status=OK");
                
        }
        return new RpcData("status=failed");
    }

    private void sendWebLinkSuggestionEmail(WebLinkModel webLink) {
        try {
            AccountInfoModel adminRec = CmAdminDao.getInstance().getAccountInfo(webLink.getAdminId());
            
            String subjectText = "Web link suggestion from: " + adminRec.getSchoolName() + " (aid=" + webLink.getAdminId() +  ")";
            String sendTo[] = {"casey@hotmath.com"};
            
            String emailText = subjectText + "\n\n" +
               "ID: " + webLink.getLinkId() + "\n" +
               "URL: " + webLink.getUrl() + "\n" +
               "Name: " +   webLink.getUrl() + "\n" +
               "Comment: " + webLink.getComments(); 
               
            SbMailManager.getInstance().sendMessage(subjectText,  emailText,  sendTo,  "admin@hotmath.com","text/plain");
        }
        catch(Exception sbe) {
            logger.error("Error sending web link suggestion email", sbe);
        }
    }

    private void deleteWebLink(final Connection conn, WebLinkModel link) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = "delete from CM_WEBLINK_LESSONS where link_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,  link.getLinkId());
            ps.executeUpdate();
            ps.close();
            
            sql = "delete from CM_WEBLINK_GROUPS where link_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,  link.getLinkId());
            
            ps.executeUpdate();
            ps.close();

            sql = "delete from CM_WEBLINK where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,  link.getLinkId());
            
            ps.executeUpdate();
        }
        finally {
            SqlUtilities.releaseResources(null, ps,  null);
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return DoWebLinksCrudOperationAction.class;
    }

}

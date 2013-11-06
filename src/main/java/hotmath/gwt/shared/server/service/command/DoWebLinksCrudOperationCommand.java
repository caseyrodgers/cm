package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.WebLinkDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import sb.mail.SbMailManager;

public class DoWebLinksCrudOperationCommand implements ActionHandler<DoWebLinksCrudOperationAction, RpcData>, ActionHandlerManualConnectionManagement {
    
    Logger logger = Logger.getLogger(DoWebLinksCrudOperationCommand.class);

    @Override
    public RpcData execute(Connection conn, DoWebLinksCrudOperationAction action) throws Exception {
        
        switch(action.getOperation()) {
        
        
            case IMPORT_TO_PUBLIC:
                action.getWebLink().setPublicLink(true);
                WebLinkDao.getInstance().importWebLink(action.getAdminId(),action.getWebLink());
                return new RpcData("status=OK");
        
            case IMPORT_FROM_PUBLIC:
                action.getWebLink().setPublicLink(false);
                WebLinkDao.getInstance().importWebLink(action.getAdminId(),action.getWebLink());
                return new RpcData("status=OK");
        
            case DELETE:
                WebLinkDao.getInstance().deleteWebLink(action.getWebLink());
                return new RpcData("status=OK");
                
                
            case UPDATE:
            case ADD:

                boolean sendSuggestEmail= true;
                if(action.getWebLink().getLinkId() > 0) {
                    // WebLinkModel webLinkModel = WebLinkDao.getInstance().getWebLink(action.getWebLink().getLinkId());
                    WebLinkDao.getInstance().deleteWebLink(action.getWebLink());
                }
                
                WebLinkDao.getInstance().addWebLink(action.getWebLink());
                
                if(action.getAdminId() != WebLinkModel.WEBLINK_DEBUG_ADMIN) {
                    if(sendSuggestEmail) {
                        sendWebLinkSuggestionEmail(action.getWebLink());
                    }
                    String linkLabel = createAdminLinkComment(action.getWebLink());
                    action.getWebLink().setComments(linkLabel);
                    WebLinkDao.getInstance().importWebLink(WebLinkModel.WEBLINK_DEBUG_ADMIN, action.getWebLink());
                }
                
                return new RpcData("status=OK");
                
        }
        return new RpcData("status=failed");
    }

    
    private String createAdminLinkComment(WebLinkModel webLink) throws Exception {
        SimpleDateFormat _labelFormat = new SimpleDateFormat("M-d HH:m");
        
        AccountInfoModel adminInfo = CmAdminDao.getInstance().getAccountInfo(webLink.getAdminId());
        
        String label = webLink.getComments() + ": (" + adminInfo.getAdminUserName() + ", " + webLink.getAdminId() + ", " + _labelFormat.format(new Date()) + ")";
        return label;
    }

    private void sendWebLinkSuggestionEmail(WebLinkModel webLink) {
        try {
            AccountInfoModel adminRec = CmAdminDao.getInstance().getAccountInfo(webLink.getAdminId());
            
            String subjectText = "Web link suggestion from: " + adminRec.getSchoolUserName() + " (aid=" + webLink.getAdminId() +  ")";
            String sendTo[] = {"casey@hotmath.com"};
            
            String emailText = subjectText + "\n\n" +
               "School Name: " + adminRec.getSchoolName() + "\n" +
               "URL: " + webLink.getUrl() + "\n" +
               "Name: " +   webLink.getName() + "\n" +
               "Comment: " + webLink.getComments() + "\n" +
               "Web Link ID: " + webLink.getLinkId();
               
            SbMailManager.getInstance().sendMessage(subjectText,  emailText,  sendTo,  "admin@hotmath.com","text/plain");
        }
        catch(Exception sbe) {
            logger.error("Error sending web link suggestion email", sbe);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return DoWebLinksCrudOperationAction.class;
    }

}

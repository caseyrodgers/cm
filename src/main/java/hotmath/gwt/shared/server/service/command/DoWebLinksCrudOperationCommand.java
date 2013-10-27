package hotmath.gwt.shared.server.service.command;

import hotmath.cm.dao.WebLinkDao;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DoWebLinksCrudOperationCommand implements ActionHandler<DoWebLinksCrudOperationAction, RpcData> {

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
                if(action.getWebLink().getLinkId() > 0) {
                    deleteWebLink(conn, action.getWebLink());
                }
                WebLinkDao.getInstance().addWebLink(action.getWebLink());
                return new RpcData("status=OK");
                
        }
        return new RpcData("status=failed");
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

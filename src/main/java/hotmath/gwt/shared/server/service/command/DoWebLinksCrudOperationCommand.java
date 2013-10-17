package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DoWebLinksCrudOperationCommand implements ActionHandler<DoWebLinksCrudOperationAction, RpcData> {

    @Override
    public RpcData execute(Connection conn, DoWebLinksCrudOperationAction action) throws Exception {
        
        switch(action.getOperation()) {
            case DELETE:
                deleteWebLink(conn, action.getWebLink());
                return new RpcData("status=OK");
                
                
            case ADD:
                deleteWebLink(conn, action.getWebLink());
                addWebLink(conn, action.getWebLink());
                return new RpcData("status=OK");
                
        }
        return new RpcData("status=failed");
    }
    
    private void deleteWebLink(final Connection conn, WebLinkModel link) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = "delete from CM_WEBLINK_LESSONS where link_id in (select id from CM_WEBLINK where admin_id = ? and name = ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,  link.getAdminId());
            ps.setString(2,  link.getName());
            ps.executeUpdate();
            ps.close();
            
            sql = "delete from CM_WEBLINK_GROUPS where link_id in (select id from CM_WEBLINK where admin_id = ? and name = ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,  link.getAdminId());
            ps.setString(2,  link.getName());
            ps.executeUpdate();
            ps.close();

            sql = "delete from CM_WEBLINK where admin_id = ? and name = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, link.getAdminId());
            ps.setString(2, link.getName());
            
            ps.executeUpdate();
        }
        finally {
            SqlUtilities.releaseResources(null, ps,  null);
        }
    }
    
    private void addWebLink(final Connection conn, WebLinkModel link) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = "insert into CM_WEBLINK(admin_id, name, url, always_available, all_groups)values(?,?,?,?,?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, link.getAdminId());
            ps.setString(2, link.getName());
            ps.setString(3,  link.getUrl());
            ps.setInt(4,  link.getAlwaysAvailable()?1:0);
            ps.setInt(5,  link.isAllGroups()?1:0);
            
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int webLinkId=0;
            if (rs.next()) {
              webLinkId = rs.getInt(1);
            }
            ps.close();
            
            if(!link.getAlwaysAvailable()) {
                sql = "insert into CM_WEBLINK_LESSONS(link_id, lesson_name, lesson_file, lesson_subject)values(?,?,?,?)";
                ps = conn.prepareStatement(sql);
                for(LessonModel lm: link.getLinkTargets()) {
                    ps.setInt(1, webLinkId);
                    ps.setString(2, lm.getLessonName());
                    ps.setString(3,  lm.getLessonFile());
                    ps.setString(4, lm.getSubject());
                    ps.executeUpdate();
                }
                ps.close();
            }
            
            if(!link.isAllGroups()) {
                sql = "insert into CM_WEBLINK_GROUPS(link_id, group_id)values(?,?)";
                ps = conn.prepareStatement(sql);
                for(GroupInfoModel g: link.getLinkGroups()) {
                    ps.setInt(1, webLinkId);
                    ps.setInt(2, g.getId());
                    ps.executeUpdate();
                }
                ps.close();
            }
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

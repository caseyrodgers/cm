package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.model.GroupInfoModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetGroupAggregateInfoAction;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetGroupAggregateInfoCommand implements ActionHandler<GetGroupAggregateInfoAction, CmList<GroupInfoModel>>{

    @Override
    public CmList<GroupInfoModel> execute(Connection conn, GetGroupAggregateInfoAction action) throws Exception {
        
        CmList<GroupInfoModel> groupInfo = new CmArrayList<GroupInfoModel>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("GROUP_NAMES_AND_COUNTS"));
            
            ps.setInt(1, action.getAdminId());
            ps.setInt(2, action.getAdminId());
            ps.setInt(3, action.getAdminId());
            ps.setInt(4, action.getAdminId());
            ps.setInt(5, action.getAdminId());
            ps.setInt(6, action.getAdminId());
            ps.setInt(7, action.getAdminId());
            ps.setInt(8, action.getAdminId());
            ps.setInt(9, action.getAdminId());
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
            	GroupInfoModel g = new GroupInfoModel(rs.getInt("admin_id"), rs.getInt("id"), rs.getString("name"),
                    	rs.getInt("student_count"), true, (rs.getInt("is_self_reg") > 0));
            	g.setSystemSelfReg(rs.getInt("is_system_self_reg") > 0);
                groupInfo.add(g);
            }
            
            return groupInfo;
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetGroupAggregateInfoAction.class;
    }
    
}

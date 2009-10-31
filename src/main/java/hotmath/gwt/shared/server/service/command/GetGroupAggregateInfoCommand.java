package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetGroupAggregateInfoAction;
import hotmath.gwt.shared.server.service.ActionHandler;
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
            
            ps.setInt(1,action.getAdminId());
            ps.setInt(2, action.getAdminId());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                groupInfo.add(new GroupInfoModel(rs.getInt("admin_id"),rs.getInt("id"), rs.getString("name"), rs.getInt("student_count")));
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

package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetUserInfoAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.HaUser;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetUserInfoCommand implements ActionHandler<GetUserInfoAction, RpcData> {

    @Override
    public RpcData execute(GetUserInfoAction action) throws Exception {
        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();
            
            HaUser user = HaUser.lookUser(conn, action.getUserId(),null);

            RpcData rpcData = new RpcData();
            rpcData.putData("uid", user.getUid());
            rpcData.putData("test_id", user.getActiveTest());
            rpcData.putData("run_id", user.getActiveTestRunId());
            rpcData.putData("test_segment", user.getActiveTestSegment());
            rpcData.putData("user_name", user.getUserName());
            rpcData.putData("session_number", user.getActiveTestRunSession());
            rpcData.putData("gui_background_style", user.getBackgroundStyle());
            rpcData.putData("test_name", user.getAssignedTestName());
            rpcData.putData("show_work_required", user.isShowWorkRequired() ? 1 : 0);
            rpcData.putData("user_account_type",user.getUserAccountType());

            int totalViewCount = getTotalInmHViewCount(conn,action.getUserId());

            rpcData.putData("view_count", totalViewCount);

            return rpcData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException(e);
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }        
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserInfoAction.class;
    }
    
    
    private int getTotalInmHViewCount(final Connection conn,int uid) throws Exception {
        PreparedStatement pstat = null;
        try {
            String sql = "select count(*) from v_HA_USER_INMH_VIEWS_TOTAL " +
                          " where item_type in ('practice','cmextra') " +
                          " and uid = ?";
            
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, uid);
            ResultSet rs = pstat.executeQuery();
            if (!rs.first())
                throw new Exception("Could not get count of viewed items");
            return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException("Error adding test run item view: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

}

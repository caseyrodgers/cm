package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetUserInfoAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.StudentUserProgramModel;
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
            
            
            CmStudentDao dao = new CmStudentDao();
            StudentModel sm = dao.getStudentModel(action.getUserId());
            StudentUserProgramModel si = dao.loadProgramInfo(conn, action.getUserId());
            StudentActiveInfo activeInfo = dao.loadActiveInfo(conn, action.getUserId());
            
            HaTestDefDao hdao = new HaTestDefDao();
            HaTestDef testDef = hdao.getTestDef(conn, si.getTestDefId());
            
            
            RpcData rpcData = new RpcData();
            rpcData.putData("uid", sm.getUid());
            rpcData.putData("test_id", activeInfo.getActiveTestId());
            rpcData.putData("run_id", activeInfo.getActiveRunId());
            rpcData.putData("test_segment", activeInfo.getActiveSegment());
            rpcData.putData("user_name", sm.getName());
            rpcData.putData("session_number", activeInfo.getActiveRunSession());
            rpcData.putData("gui_background_style", sm.getBackgroundStyle());
            rpcData.putData("test_name", testDef.getName());
            rpcData.putData("show_work_required", sm.getShowWorkRequired() ? 1 : 0);
            rpcData.putData("user_account_type","student");
            rpcData.putData("pass_percent_required",si.getPassPercent());
            rpcData.putData("test_segment_count", testDef.getTotalSegmentCount());

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

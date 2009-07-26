package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetUserInfoAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.ChapterInfo;
import hotmath.testset.ha.HaTestConfig;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class GetUserInfoCommand implements ActionHandler<GetUserInfoAction, UserInfo> {

    @Override
    public UserInfo execute(GetUserInfoAction action) throws Exception {
        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();
            
            
            CmStudentDao dao = new CmStudentDao();
            StudentModel sm = dao.getStudentModel(action.getUserId());
            StudentUserProgramModel si = dao.loadProgramInfo(conn, action.getUserId());
            StudentActiveInfo activeInfo = dao.loadActiveInfo(conn, action.getUserId());
            
            HaTestDefDao hdao = new HaTestDefDao();
            HaTestDef testDef = hdao.getTestDef(conn, si.getTestDefId());
            
            ChapterInfo chapterInfo = hdao.getChapterInfo(conn, si);

            /** Create title sections.  The main title
             *  will contain the chapter # (if any).  The
             *  subtitle will contain the chapter title (if any)
             */
            String testTitle = testDef.getName();
            String subTitle=null;
            if(chapterInfo != null) {
                testTitle += ", #" + chapterInfo.getChapterNumber();
                subTitle = chapterInfo.getChapterTitle();
            }
            
            UserInfo userInfo = new UserInfo();
            userInfo.setUid(sm.getUid());
            userInfo.setTestId(activeInfo.getActiveTestId());
            userInfo.setRunId(activeInfo.getActiveRunId());
            userInfo.setTestSegment(activeInfo.getActiveSegment());
            userInfo.setUserName(sm.getName());
            userInfo.setSessionNumber(activeInfo.getActiveRunSession());
            userInfo.setBackgroundStyle(sm.getBackgroundStyle());
            userInfo.setTestName(testTitle);
            userInfo.setSubTitle(subTitle);
            userInfo.setShowWorkRequired(sm.getShowWorkRequired());
            userInfo.setPassPercentRequired(si.getPassPercent());
            userInfo.setTestSegmentCount(testDef.getTotalSegmentCount());
            userInfo.setViewCount(getTotalInmHViewCount(conn,action.getUserId()));
            
            return userInfo;
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

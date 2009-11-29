package hotmath.gwt.shared.server.service.command;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetUserInfoAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.UserInfo;
import hotmath.gwt.shared.client.util.UserInfo.AccountType;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.ha.ChapterInfo;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.StudentUserProgramModel;

import java.sql.Connection;

public class GetUserInfoCommand implements ActionHandler<GetUserInfoAction, UserInfo> {

    @Override
    public UserInfo execute(final Connection conn, GetUserInfoAction action) throws Exception {
        try {
            CmStudentDao dao = new CmStudentDao();
            StudentModelI sm = dao.getStudentModelBasic(conn, action.getUserId());
            
            CmUserProgramDao upDao = new CmUserProgramDao();
            StudentUserProgramModel si = upDao.loadProgramInfoCurrent(conn, action.getUserId());
            StudentActiveInfo activeInfo = dao.loadActiveInfo(conn, action.getUserId());
            
            
            AccountType accountType = new CmAdminDao().getAccountType(conn, sm.getAdminUid());
            
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
            userInfo.setTutoringAvail(sm.getTutoringAvail());
            userInfo.setPassword(sm.getPasscode());
            userInfo.setLoginName(null);
            userInfo.setDemoUser(sm.getIsDemoUser());

            /** Set number of sessions in current prescription */
            if(userInfo.getRunId() > 0)
                userInfo.setSessionCount(new HaTestRunDao().getTestRunLessons(conn,userInfo.getRunId()).size());
            
            userInfo.setUserAccountType(accountType);
            
            userInfo.setPassPercentRequired(si.getConfig().getPassPercent());
            userInfo.setTestSegmentCount(testDef.getTotalSegmentCount());
            userInfo.setViewCount(dao.getTotalInmHViewCount(conn,action.getUserId()));
            
            return userInfo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException(e);
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetUserInfoAction.class;
    }
    
}

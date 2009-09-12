package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.HotMathSubscriberManager;
import hotmath.testset.ha.ChapterInfo;
import hotmath.testset.ha.HaLoginInfo;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaUser;
import hotmath.testset.ha.StudentUserProgramModel;

import java.sql.Connection;

/** Create Student accounts based on StudentModel passed in (as template)
 *  and list of name/passwords.
 *  
 *  Either create all ... or none of the accounts.
 **/
public class CreateAutoRegistrationAccountCommand implements ActionHandler<CreateAutoRegistrationAccountAction, RpcData> {

    
    /** Return RpcData with fields:
     * 
     *    key = new login key
     */
    public RpcData execute(Connection conn, CreateAutoRegistrationAccountAction action) throws Exception {
        CmStudentDao dao = new CmStudentDao();
        StudentModel studentModel = dao.getStudentModel(action.getUserId(), true);

        studentModel.setPasscode(action.getPassword());
        studentModel.setName(action.getUser());
        studentModel = dao.addStudent(conn, studentModel);

        
        StudentUserProgramModel si = dao.loadProgramInfo(conn, studentModel.getUid());
        StudentActiveInfo activeInfo = dao.loadActiveInfo(conn, studentModel.getUid());
        
        String subscriberId = new CmAdminDao().getAccountInfo(studentModel.getAdminUid()).getSubscriberId();
        HotMathSubscriber sub = HotMathSubscriberManager.findSubscriber(subscriberId); 
        
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
        userInfo.setUid(studentModel.getUid());
        userInfo.setTestId(activeInfo.getActiveTestId());
        userInfo.setRunId(activeInfo.getActiveRunId());
        userInfo.setTestSegment(activeInfo.getActiveSegment());
        userInfo.setUserName(studentModel.getName());
        userInfo.setSessionNumber(activeInfo.getActiveRunSession());
        userInfo.setBackgroundStyle(studentModel.getBackgroundStyle());
        userInfo.setTestName(testTitle);
        userInfo.setSubTitle(subTitle);
        userInfo.setShowWorkRequired(studentModel.getShowWorkRequired());
        userInfo.setTutoringAvail(studentModel.getTutoringAvail());
        
        userInfo.setUserAccountType(sub.getSubscriberType());
        
        userInfo.setPassPercentRequired(si.getConfig().getPassPercent());
        userInfo.setTestSegmentCount(testDef.getTotalSegmentCount());
        userInfo.setViewCount(dao.getTotalInmHViewCount(conn,action.getUserId()));
        
        
        // Make a new HA_USER_LOGIN entry and return key
        HaUser huser = new HaUser();
        huser.setUid(userInfo.getUid());
        
        RpcData rdata = new RpcData();
        rdata.putData("key", HaLoginInfo.addLoginInfo(huser));
        return rdata;
    }
    
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateAutoRegistrationAccountAction.class;
    }
    
}

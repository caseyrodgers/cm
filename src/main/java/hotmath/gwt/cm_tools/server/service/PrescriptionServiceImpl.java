package hotmath.gwt.cm_tools.server.service;

import hotmath.HotMathUtilities;
import hotmath.ProblemID;
import hotmath.SolutionManager;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.AutoUserAdvanced;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.cm_tools.client.service.PrescriptionService;
import hotmath.gwt.shared.client.rpc.action.AddGroupAction;
import hotmath.gwt.shared.client.rpc.action.AddStudentAction;
import hotmath.gwt.shared.client.rpc.action.AutoAdvanceUserAction;
import hotmath.gwt.shared.client.rpc.action.GetLessonItemsForTestRunAction;
import hotmath.gwt.shared.client.rpc.action.GetPrescriptionAction;
import hotmath.gwt.shared.client.rpc.action.GetProgramDefinitionsAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlCheckedAction;
import hotmath.gwt.shared.client.rpc.action.GetQuizResultsHtmlAction;
import hotmath.gwt.shared.client.rpc.action.GetReviewHtmlAction;
import hotmath.gwt.shared.client.rpc.action.GetSolutionAction;
import hotmath.gwt.shared.client.rpc.action.GetUserInfoAction;
import hotmath.gwt.shared.client.rpc.action.GetViewedInmhItemsAction;
import hotmath.gwt.shared.client.rpc.action.SaveFeedbackAction;
import hotmath.gwt.shared.client.rpc.action.SaveQuizCurrentResultAction;
import hotmath.gwt.shared.client.rpc.action.SetInmhItemAsViewedAction;
import hotmath.gwt.shared.client.rpc.action.UpdateStudentAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;
import hotmath.gwt.shared.server.service.ActionDispatcher;
import hotmath.solution.Solution;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestRunResult;
import hotmath.testset.ha.HaUser;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PrescriptionServiceImpl extends RemoteServiceServlet implements PrescriptionService {
    
    private static final long serialVersionUID = 1034624620689798799L;
    
    Logger logger = Logger.getLogger(PrescriptionServiceImpl.class);

    public PrescriptionServiceImpl() {
        logger.info("PrescriptionServiceImpl Created");
    }

    public RpcData getPrescriptionSessionJson(int runId, int sessionNumber, boolean updateActiveInfo) throws CmRpcException {
        GetPrescriptionAction getPresAction = new GetPrescriptionAction(runId,sessionNumber, updateActiveInfo);
        return ActionDispatcher.getInstance().execute(getPresAction);
    }

    public ArrayList<RpcData> getViewedInmhItems(int runId) throws CmRpcException {
        GetViewedInmhItemsAction getViewedAction = new GetViewedInmhItemsAction(runId);
        List<RpcData> rdata = ActionDispatcher.getInstance().execute(getViewedAction).getRpcData();
        
        return (ArrayList<RpcData>)rdata;
    }
    
    
    public RpcData getSolutionHtml(int userId, String pid) throws CmRpcException {
        GetSolutionAction getViewedAction = new GetSolutionAction(userId, pid);
        return ActionDispatcher.getInstance().execute(getViewedAction);
    }

    public void setInmhItemAsViewed(int runId, String type, String file) throws CmRpcException {
        SetInmhItemAsViewedAction getViewedAction = new SetInmhItemAsViewedAction(runId,type,file);
        ActionDispatcher.getInstance().execute(getViewedAction);
    }

    public UserInfo getUserInfo(int uid) throws CmRpcException {
        GetUserInfoAction action = new GetUserInfoAction(uid);
        return ActionDispatcher.getInstance().execute(action);
    }
    
    public RpcData getQuizHtml(int uid, int testSegment) throws CmRpcException {
        GetQuizHtmlAction action = new GetQuizHtmlAction(uid, testSegment);
        return ActionDispatcher.getInstance().execute(action);
    }

    public RpcData getQuizHtmlChecked(int testId) throws CmRpcException {
        GetQuizHtmlCheckedAction action = new GetQuizHtmlCheckedAction(testId);
        return ActionDispatcher.getInstance().execute(action);
    }
    
    public void saveFeedback(String comments, String commentsUrl, String stateInfo) throws CmRpcException {
        SaveFeedbackAction action = new SaveFeedbackAction(comments, commentsUrl, stateInfo);
        ActionDispatcher.getInstance().execute(action);
    }   
    
    public AutoUserAdvanced autoAdvanceUser(Integer userId) throws CmRpcException {
        AutoAdvanceUserAction action = new AutoAdvanceUserAction(userId);
        return ActionDispatcher.getInstance().execute(action);
    }
    
    public List<SubjectModel> getSubjectDefinitions(String progId) throws CmRpcException {
        GetProgramDefinitionsAction action = new GetProgramDefinitionsAction(progId);
        return (List<SubjectModel>)ActionDispatcher.getInstance().execute(action);
    }
    
    public StudentModel updateUser(StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew, Boolean passcodeChanged) throws CmRpcException {
        UpdateStudentAction action = new UpdateStudentAction(sm, stuChanged, progChanged, progIsNew, passcodeChanged);
        return ActionDispatcher.getInstance().execute(action);
    }

    public GroupModel addGroup(Integer adminUid, GroupModel gm) throws CmRpcException {
        AddGroupAction action = new AddGroupAction(adminUid, gm);
        return ActionDispatcher.getInstance().execute(action);
    }

    public List<LessonItemModel> getLessonItemsForTestRun(Integer runId) throws CmRpcException {
        GetLessonItemsForTestRunAction action = new GetLessonItemsForTestRunAction(runId);
        return ActionDispatcher.getInstance().execute(action);
    }
    
    

    @Override
    public String getSolutionProblemStatementHtml(String pid) {
        try {
            Solution sol = SolutionManager.getSolution(pid);
            String probStatementHtml = sol.getStatement(false);

            ProblemID ppid = new ProblemID(pid);
            String path = ppid.getSolutionPath_DirOnly("solutions");
            probStatementHtml = HotMathUtilities.makeAbsolutePaths(path, probStatementHtml);

            return probStatementHtml;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

   

    /**
     * Return the HTML content in the hm_content of the named file in the
     * hotmath space.
     * 
     * @param file
     * @param baseDirectory
     *            The directory relative images should point
     * 
     * @return
     */
    public RpcData getHmContent(String file, String baseDirectory) throws CmRpcException {
        GetReviewHtmlAction action = new GetReviewHtmlAction(file, baseDirectory);
        return ActionDispatcher.getInstance().execute(action);
    }

    /**
     * Method used to get a list of RcpData objects defining test results for
     * the current test.
     * 
     * @return ArrayList of RpcData objects
     * 
     *         __gwt.typeArgs <hotmath.gwt.cm.client.util.RpcData>
     */
    public ArrayList<RpcData> getQuizCurrentResults(int userId) throws CmRpcException {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            ArrayList<RpcData> rpcData = new ArrayList<RpcData>();
            int testId = HaUser.lookUser(conn, userId,null).getActiveTest();
            if(testId == 0)
                return rpcData;
            
            // TODO: following may not be needed...
            HaTest test = HaTestDao.loadTest(conn,testId);
            
            List<HaTestRunResult> testResults = HaTestDao.getTestCurrentResponses(conn, testId);
            
            for (HaTestRunResult tr : testResults) {
                if (tr.isAnswered()) {
                    RpcData rd = new RpcData(Arrays.asList("pid=" + tr.getPid(), "answer=" + tr.getResponseIndex()));
                    rpcData.add(rd);
                }
            }
            return rpcData;
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
    }
    
    

    /**
     * @deprecated (use Command of same name)
     * 
     */
    public void saveWhiteboardData(int uid, int runId, String pid, String command, String commandData)
            throws CmRpcException {
        throw new CmRpcException("use the action");
    
    }

    /** @deprecated use GetWhiteboardDataAction
     * 
     */
    public ArrayList<RpcData> getWhiteboardData(int uid, String pid) throws CmRpcException {
        throw new CmRpcException("Use the GetWhiteboardDataCommand");
    }

    public void saveQuizCurrentResult(int testId, boolean correct, int answerIndex, String pid) throws CmRpcException {
        SaveQuizCurrentResultAction action = new SaveQuizCurrentResultAction(testId, correct, answerIndex, pid);
        ActionDispatcher.getInstance().execute(action);
    }


    public void resetUser(int userId) throws CmRpcException {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            HaUser user = HaUser.lookUser(conn, userId,null);
            user.setActiveTest(0);
            user.setActiveTestRunId(0);
            user.setActiveTestSegment(0);
            user.setActiveTestRunSession(0);

            StudentActiveInfo activeInfo = new StudentActiveInfo();
            activeInfo.setActiveSegmentSlot(0);
            new CmStudentDao().setActiveInfo(conn, userId, activeInfo);

            user.update(conn);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }
    }

    public RpcData getQuizResultsHtml(int runId) throws CmRpcException {
        GetQuizResultsHtmlAction action = new GetQuizResultsHtmlAction(runId);
        return ActionDispatcher.getInstance().execute(action);
    }
    
    public StudentModel addUser(StudentModel sm) throws CmRpcException {
        AddStudentAction action = new AddStudentAction(sm);
        return ActionDispatcher.getInstance().execute(action);
    }    

    public void setUserBackground(int userId, String backgroundStyle) throws CmRpcException {
        throw new CmRpcException("Use the action");
    }


    /** @deprecated use GetStudentShowWorkAction
     * 
     */
    public List<StudentShowWorkModel> getStudentShowWork(Integer uid, Integer runId) throws CmRpcException {
        throw new CmRpcException("Use the action");
    }

    public List<StudentActivityModel> getStudentActivity(StudentModel sm) throws CmRpcException {
        try {
            CmStudentDao dao = new CmStudentDao();
            return dao.getStudentActivity(sm.getUid());
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }



    public StudentModel getStudentModel(Integer uid) throws CmRpcException {
        try {
            CmStudentDao dao = new CmStudentDao();
            return dao.getStudentModel(uid);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public List<StudyProgramModel> getProgramDefinitions() throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.getProgramDefinitions();
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }


    public List<GroupModel> getActiveGroups(Integer adminUid) throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.getActiveGroups(adminUid);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    public List<ChapterModel> getChaptersForProgramSubject(String progId, String subjId) throws CmRpcException {
        try {
            CmAdminDao cma = new CmAdminDao();
            return cma.getChaptersForProgramSubject(progId, subjId);
        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }


    
    

    /**
     * Simple test to pound the getting of solution html to see if it is
     * responsible for the Too Many Files error.
     * 
     * I suspect it was the cause, or at least the many instances of
     * SolutionHTMLCreatorIimplVelocity was.
     * 
     * @param as
     */
    static public void main(String as[]) {
        try {
            PrescriptionServiceImpl pi = new PrescriptionServiceImpl();

            for (int i = 0; i < 50; i++) {
                Connection conn = null;
                PreparedStatement pstat = null;
                try {
                    String sql = "select problemindex from SOLUTIONS where booktitle = 'genericalg1'";

                    conn = HMConnectionPool.getConnection();
                    pstat = conn.prepareStatement(sql);

                    ResultSet rs = pstat.executeQuery();
                    while (rs.next()) {
                        String pid = rs.getString("problemindex");

                        pi.getSolutionHtml(700, pid);
                    }

                } finally {
                    SqlUtilities.releaseResources(null, pstat, conn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

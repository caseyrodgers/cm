package hotmath.gwt.shared.server.service.command;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.TestSet;
import hotmath.testset.ha.ChapterInfo;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestDefFactory;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.VelocityTemplateFromStringManager;
import hotmath.util.sql.SqlUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


public class GetQuizHtmlCommand implements ActionHandler<GetQuizHtmlAction, RpcData> {

    static final Logger logger = Logger.getLogger(GetQuizHtmlCommand.class);
    
    @Override
    public RpcData execute(final Connection conn, GetQuizHtmlAction action) throws Exception {
        
        int testSegment = action.getTestSegment();
        int uid = action.getUid();

        try {
            String quizHtmlTemplate = readQuizHtmlTemplate();
            Map<String, Object> map = new HashMap<String, Object>();

            // StudentModel sm = dao.getStudentModel(uid);
            //HaUser user = HaUser.lookUser(conn, uid,null);
            
            CmUserProgramDao upDao = new CmUserProgramDao();
            StudentUserProgramModel programInfo = upDao.loadProgramInfoCurrent(conn, uid);

            String testName = programInfo.getTestName();

            if (testSegment == 0)
                testSegment = 1;
            
            CmStudentDao dao = new CmStudentDao();
            StudentActiveInfo activeInfo = dao.loadActiveInfo(conn, uid);

            int testSegmentSlot = activeInfo.getActiveSegmentSlot();
            
            /** If user is re-taking the current segment, then move to next
             * slot.
             */
            
            /** TODO: how to know if this a retake?
             * 
             */
            
            /** determine the quiz slot to use.  
             * 
             * We reuse the slot if:
             * 
             * 1. user has never seen this quiz.
             * 2. user has never seen this quiz segment.
             * 3. user passed last quiz segment.
             * 
             * 
             * We increment the slot if:
             * 
             * 1. user failed current segment
             * 2. user is re-taking same segment
             * 
             * 
             */
            
            /** Check Cache for this exact test HTML.  Make sure it is unique
             * in case program changes slightly.
             *  
             */
            
            //String testKey = programInfo.toString() + " Segment=" + testSegment + "_" + testSegmentSlot;
            //RpcData rpcDataCached = (RpcData)CmCacheManager.getInstance().retrieveFromCache(CacheName.TEST_HTML,testKey);
            //if(rpcDataCached != null) {
            //    return rpcDataCached;
            //}

            boolean isActiveTest = activeInfo.getActiveTestId() > 0;
            
            HaTest haTest = null;
            if (false && isActiveTest && testSegment == activeInfo.getActiveSegment()) {
                // reuse the existing test
                haTest = HaTestDao.loadTest(conn, activeInfo.getActiveTestId());
            } else {
                // register a new test
                HaTestDef testDef = HaTestDefFactory.createTestDef(conn,testName);
                haTest = HaTestDao.createTest(conn, uid, testDef, testSegment);
            }

            String testTitle = haTest.getTitle();   

            TestSet _testSet = new TestSet(haTest.getPids());

            HaTestDefDao tdo = new HaTestDefDao();
            
            map.put("haTest", haTest);
            map.put("testTitle", testTitle);
            map.put("testSet", _testSet);
            
            ChapterInfo chapterInfo = tdo.getChapterInfo(conn, programInfo);

            String subTitle=null;
            if(chapterInfo != null) {
                testTitle += ", #" + chapterInfo.getChapterNumber();
                subTitle = chapterInfo.getChapterTitle();
            }            

            map.put("subTitle", "");

            String quizHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(quizHtmlTemplate, map);

            RpcData rpcData = new RpcData();
            rpcData.putData("quiz_html", quizHtml);
            rpcData.putData("test_id", haTest.getTestId());
            rpcData.putData("quiz_segment", testSegment);
            rpcData.putData("quiz_segment_count", haTest.getTotalSegments());
            rpcData.putData("title", testTitle);
            rpcData.putData("sub_title", subTitle);
            
            // CmCacheManager.getInstance().addToCache(CacheName.TEST_HTML,testKey, rpcData);

            return rpcData;
        } catch (Exception e) {
            throw e;
        }
    }
    
//    public RpcData getQuizHtml(int testId) throws CmRpcException {
//        try {
//
//            String quizHtmlTemplate = GetQuizHtmlCommand.readQuizHtmlTemplate();
//            Map<String, Object> map = new HashMap<String, Object>();
//
//            HaTest haTest = HaTest.loadTest(testId);
//            String testTitle = haTest.getTitle();
//
//            TestSet _testSet = new TestSet(haTest.getPids());
//
//            int testSegment = haTest.getSegment();
//            map.put("haTest", haTest);
//            map.put("testTitle", testTitle);
//            map.put("testSet", _testSet);
//            map.put("subTitle", testSegment);
//
//            String quizHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(quizHtmlTemplate, map);
//
//            RpcData rpcData = new RpcData();
//            rpcData.putData("quiz_html", quizHtml);
//            rpcData.putData("test_id", haTest.getTestId());
//            rpcData.putData("quiz_segment", testSegment);
//            rpcData.putData("title", testTitle);
//
//            return rpcData;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new CmRpcException(e.getMessage());
//        }
//    }    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetQuizHtmlAction.class;
    }
    
    

    /** Read the velocity template from the classpath
     * 
     * @return
     * @throws IOException
     */
    static public String readQuizHtmlTemplate() throws IOException {
        InputStream is = GetQuizHtmlCommand.class.getResourceAsStream("quiz_template.vm");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
    

}

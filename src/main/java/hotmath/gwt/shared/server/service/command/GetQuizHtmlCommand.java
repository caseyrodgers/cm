package hotmath.gwt.shared.server.service.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.TestSet;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefFactory;
import hotmath.testset.ha.HaUser;
import hotmath.util.HMConnectionPool;
import hotmath.util.VelocityTemplateFromStringManager;
import hotmath.util.sql.SqlUtilities;

public class GetQuizHtmlCommand implements ActionHandler<GetQuizHtmlAction, RpcData> {

    @Override
    public RpcData execute(GetQuizHtmlAction action) throws Exception {
        
        int testSegment = action.getTestSegment();
        int uid = action.getUid();
        
        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();
            String quizHtmlTemplate = readQuizHtmlTemplate();
            Map<String, Object> map = new HashMap<String, Object>();

            HaUser user = HaUser.lookUser(conn, uid,null);
            String testName = user.getAssignedTestName();

            if (testSegment == 0)
                testSegment = 1;

            boolean isActiveTest = user.getActiveTest() > 0;
            HaTest haTest = null;
            if (isActiveTest && testSegment == user.getActiveTestSegment()) {
                // reuse the existing test
                haTest = HaTest.loadTest(conn,user.getActiveTest());
            } else {
                // register a new test
                HaTestDef testDef = HaTestDefFactory.createTestDef(conn,testName);
                haTest = HaTest.createTest(conn,uid, testDef, testSegment);
            }

            String testTitle = haTest.getTitle();

            TestSet _testSet = new TestSet(haTest.getPids());

            map.put("haTest", haTest);
            map.put("testTitle", testTitle);
            map.put("testSet", _testSet);
            map.put("subTitle", haTest.getSubTitle(testSegment));

            String quizHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(quizHtmlTemplate, map);

            RpcData rpcData = new RpcData();
            rpcData.putData("quiz_html", quizHtml);
            rpcData.putData("test_id", haTest.getTestId());
            rpcData.putData("quiz_segment", testSegment);
            rpcData.putData("quiz_segment_count", haTest.getTotalSegments());
            rpcData.putData("title", testTitle);

            return rpcData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
        }

        return null;
        
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

package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlCheckedAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.testset.TestSet;
import hotmath.testset.ha.HaTest;
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

public class GetQuizHtmlCheckedCommand implements ActionHandler<GetQuizHtmlCheckedAction, RpcData> {

    @Override
    public RpcData execute(GetQuizHtmlCheckedAction action) throws Exception {
        Connection conn=null;
        try {

            String quizHtmlTemplate = GetQuizHtmlCommand.readQuizHtmlTemplate();
            Map<String, Object> map = new HashMap<String, Object>();

            conn = HMConnectionPool.getConnection();
            HaTest haTest = HaTest.loadTest(conn, action.getTestId());
            String testTitle = haTest.getTitle();

            TestSet _testSet = new TestSet(haTest.getPids());

            int testSegment = haTest.getSegment();
            map.put("haTest", haTest);
            map.put("testTitle", testTitle);
            map.put("testSet", _testSet);
            map.put("subTitle", testSegment);

            String quizHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(quizHtmlTemplate, map);

            RpcData rpcData = new RpcData();
            rpcData.putData("quiz_html", quizHtml);
            rpcData.putData("test_id", haTest.getTestId());
            rpcData.putData("quiz_segment", testSegment);
            rpcData.putData("title", testTitle);

            return rpcData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CmRpcException(e.getMessage());
        }
        finally {
            SqlUtilities.releaseResources(null,null, conn);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetQuizHtmlCheckedAction.class;
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

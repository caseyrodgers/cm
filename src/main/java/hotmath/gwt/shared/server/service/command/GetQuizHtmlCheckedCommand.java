package hotmath.gwt.shared.server.service.command;

import hotmath.cm.test.HaTestSet;
import hotmath.cm.test.HaTestSetQuestion;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlCheckedAction;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.util.VelocityTemplateFromStringManager;
import hotmath.util.sql.SqlUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/** Get the quiz HTML with the answers in place
 * 
 * @TODO: replace RpcData with specific HtmlChecked
 * 
 */
public class GetQuizHtmlCheckedCommand implements ActionHandler<GetQuizHtmlCheckedAction, RpcData> {

	private static Logger logger = Logger.getLogger(GetQuizHtmlCheckedCommand.class);
   
    @Override
    public RpcData execute(final Connection autoConn, GetQuizHtmlCheckedAction action) throws Exception {
        
        RpcData rpcDataCached = (RpcData)CmCacheManager.getInstance().retrieveFromCache(CacheName.TEST_HTML_CHECKED, action.getTestId());
        if(rpcDataCached != null)
            return rpcDataCached;
        try {

            String quizHtmlTemplate = GetQuizHtmlCommand.readQuizHtmlTemplate();

            HaTest haTest = HaTestDao.getInstance().loadTest( action.getTestId());
            
            //TODO:  if Custom Quiz, then get CQ name as title
            String testTitle;
            if ("custom quiz".equalsIgnoreCase(haTest.getTitle())) {
            	CustomQuizDef cqDef = CmQuizzesDao.getInstance().getCustomQuizByTestId(action.getTestId());
            	testTitle = "CQ: " + cqDef.getQuizName();
            }
            else {
            	testTitle = haTest.getTitle();
            }

            HaTestSet _testSet = new HaTestSet(autoConn, haTest.getPids());
            int testSegment = haTest.getSegment();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("haTest", haTest);
            map.put("testTitle", testTitle);
            map.put("testSet", _testSet);
            map.put("subTitle", testSegment);

            List<HaTestSetQuestion> questions = _testSet.getQuestions();

            String quizHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(quizHtmlTemplate, map);

            RpcData rpcData = new RpcData();
            rpcData.putData("quiz_html", quizHtml);
            rpcData.putData("test_id", haTest.getTestId());
            rpcData.putData("quiz_segment", testSegment);
            rpcData.putData("title", testTitle);

            CmCacheManager.getInstance().addToCache(CacheName.TEST_HTML_CHECKED, action.getTestId(), rpcData);

            return rpcData;
        } catch (Exception e) {
        	logger.error(String.format("*** Error executing Action: %s", action.toString()), e);
            throw new CmRpcException(e.getMessage());
        }
        finally {
            SqlUtilities.releaseResources(null,null, null);
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

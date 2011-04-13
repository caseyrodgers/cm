package hotmath.gwt.shared.server.service.command;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.test.HaTestSet;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetQuizCurrentResultsAction;
import hotmath.testset.ha.ChapterInfo;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.VelocityTemplateFromStringManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sb.util.SbFile;



/** Return the requested, existing QUIZ html 
 *  named by the action.testId
 *  
 *  it is an an error for the text to not exist.
 *  
 * @author casey
 *
 */
public class GetQuizHtmlCommand implements ActionHandler<GetQuizHtmlAction, QuizHtmlResult> {

    static final Logger logger = Logger.getLogger(GetQuizHtmlCommand.class);
    @Override
    public QuizHtmlResult execute(final Connection conn, GetQuizHtmlAction action) throws Exception {

        if (action.getTestId() == 0) {
            throw new CmRpcException("Invalid QUIZ request: " + action);
        }

        try {
            String quizHtmlTemplate = readQuizHtmlTemplate();
            Map<String, Object> map = new HashMap<String, Object>();

            StudentUserProgramModel programInfo = new CmUserProgramDao().loadProgramInfoForTest(conn, action.getTestId());

            
            int testId = action.getTestId();

            HaTest haTest = HaTestDao.loadTest(conn, testId);
            
            String testTitle = null;
            if(programInfo.getCustomProgramId() > 0) {
                testTitle = new CmCustomProgramDao().getCustomProgram(conn,programInfo.getCustomProgramId()).getProgramName();
            }
            else if(programInfo.getCustomQuizId() > 0) {
                testTitle =  programInfo.getCustomQuizName();
            }
            else {
                testTitle = haTest.getTitle();
            }
                        
            
            /** is this quiz HTML already in the cache?
             *   
             *  we keep both the testSet and the proceed/sprited quiz
             *  html in the cache via the QuizCachedInfo.
             */
            HaTestDef testDef = haTest.getTestDef();
            String cacheKey = testDef.getTestDefId() + "_" + testDef.getTestInitJson(haTest) + "_" + haTest.getSegment() + "_" + haTest.getSegmentSlot();
            QuizCacheInfo cacheInfo = (QuizCacheInfo)CmCacheManager.getInstance().retrieveFromCache(CacheName.TEST_HTML, cacheKey);
            if(cacheInfo == null) {
                logger.info("Create quiz HTML and add to cache");
                
                HaTestSet testSet = new HaTestSet(conn,haTest.getPids());
                
                map.put("haTest", haTest);
                map.put("testTitle", testTitle);
                map.put("testSet", testSet);
                map.put("subTitle", "");
    
                String quizHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(quizHtmlTemplate, map);
                //  quizHtml = processHtmlForSprites(quizHtml);
                
                ChapterInfo chapterInfo = new HaTestDefDao().getChapterInfo(conn, programInfo);
                String subTitle=null;
                if(chapterInfo != null) {
                    testTitle += ", #" + chapterInfo.getChapterNumber();
                    subTitle = chapterInfo.getChapterTitle();
                }
                else {
                    subTitle = haTest.getSubTitle(haTest.getSegment());
                }  

                String spritedHtml = quizHtml; //  processHtmlForSprites(quizHtml);
                
                cacheInfo = new QuizCacheInfo(spritedHtml,testSet, subTitle);
                CmCacheManager.getInstance().addToCache(CacheName.TEST_HTML, cacheKey, cacheInfo,true);
            }
            else {
                logger.info("Retrieved quiz HTML from cache");
            }
            
            GetQuizCurrentResultsAction resultsAction = new GetQuizCurrentResultsAction(action.getTestId());
            CmList<RpcData> currentResponses = new GetQuizCurrentResultsCommand().execute(conn, resultsAction);

            QuizHtmlResult result = new QuizHtmlResult();
            result.setQuizHtml(cacheInfo.quizHtml);
            result.setTestId(haTest.getTestId());
            result.setQuizSegment(haTest.getSegment());
            
            
            int segmentCount=0;
            if(programInfo.getCustomProgramId() > 0) {
                segmentCount = new CmCustomProgramDao().getTotalSegmentCount(conn, programInfo.getCustomProgramId());
            }
            else {
                segmentCount = haTest.getTotalSegments();
            }
            result.setQuizSegmentCount(segmentCount);
            
            result.setTitle(testTitle);
            result.setSubTitle(cacheInfo.subTitle);
            result.setTestId(haTest.getTestId());
            result.setAnswers(cacheInfo.testSet.getAnswers());
            result.setCurrentSelections(currentResponses);
            
            
            return result;
            
        } catch (Exception e) {
            /** for catching error during debugging */
            throw e;
        }
    }
    
    private String processHtmlForSprites(String quizHtml) throws Exception {
        String fileBase = CmWebResourceManager.getInstance().getRetainedFileBase();
        File quizSprited = new File(fileBase,"quiz_" + System.currentTimeMillis());
        quizSprited.mkdirs();
        /** write to sprited file */
        new CreateQuizSprited(quizSprited, quizHtml).createQuizSpritedHtml();
        
        /** return from sprited file */
        quizHtml = new SbFile(new File(quizSprited, "tutor_steps-sprited.html")).getFileContents().toString("\n");
        return quizHtml;
    }   

    
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






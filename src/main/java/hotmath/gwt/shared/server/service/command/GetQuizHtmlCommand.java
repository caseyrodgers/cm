package hotmath.gwt.shared.server.service.command;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.cm.test.HaTestSet;
import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.shared.client.rpc.action.GetQuizHtmlAction;
import hotmath.gwt.shared.client.rpc.result.QuizHtmlResult;
import hotmath.testset.ha.ChapterInfo;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestDefFactory;
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

import sb.util.MD5;


public class GetQuizHtmlCommand implements ActionHandler<GetQuizHtmlAction, QuizHtmlResult> {

    static final Logger logger = Logger.getLogger(GetQuizHtmlCommand.class);
    
    
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
    @Override
    public QuizHtmlResult execute(final Connection conn, GetQuizHtmlAction action) throws Exception {

        
        
        new CmStudentDao().verifyActiveProgram(conn, action.getTestId());

        int testSegment = action.getTestSegment();
        int uid = action.getUid();

        try {
            String quizHtmlTemplate = readQuizHtmlTemplate();
            Map<String, Object> map = new HashMap<String, Object>();

            CmUserProgramDao upDao = new CmUserProgramDao();
            StudentUserProgramModel programInfo = upDao.loadProgramInfoCurrent(conn, uid);

            String testName = programInfo.getTestName();

            if (testSegment == 0)
                testSegment = 1;
            
            CmStudentDao dao = new CmStudentDao();
            StudentActiveInfo activeInfo = dao.loadActiveInfo(conn, uid);

            HaTest haTest=null;
            int activeTest = activeInfo.getActiveTestId();
            if(activeTest > 0 && activeInfo.getActiveSegment() == testSegment) {
                /** load an existing test
                 * 
                 */
                haTest = HaTestDao.loadTest(conn, activeTest);
                uid = haTest.getUser().getUserKey();
                testSegment = haTest.getSegment();
            }
            else {
                /** create and register a new test
                 */
                HaTestDef testDef = HaTestDefFactory.createTestDef(conn,testName);
                haTest = HaTestDao.createTest(conn, uid, testDef, testSegment);
            }
            String testTitle = haTest.getTitle();            

            HaTestSet _testSet = new HaTestSet(conn,haTest.getPids());

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

            //quizHtml = processHtmlForSprites(quizHtml);

            QuizHtmlResult result = new QuizHtmlResult();
            result.setUserId(uid);
            result.setQuizHtml(quizHtml);
            result.setTestId(haTest.getTestId());
            result.setQuizSegment(testSegment);
            result.setQuizSegmentCount(haTest.getTotalSegments());
            result.setTitle(testTitle);
            result.setSubTitle(subTitle);
            result.setTestId(haTest.getTestId());
            result.setAnswers(_testSet.getAnswers());
            return result;
        } catch (Exception e) {
            throw e;
        }
    }
    
    
    private String processHtmlForSprites(String quizHtml) throws Exception {
        String md5OfThis = MD5.getMD5(quizHtml);
        String fileBase = CmWebResourceManager.getInstance().getFileBase();
        File quizSprited = new File(fileBase,md5OfThis);
        if(!quizSprited.exists()) {
            quizSprited.mkdirs();
            new CreateQuizSprited(quizSprited, quizHtml).createQuizSpritedHtml();
        }
        
        /** read html with sprited images */
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

class CreateQuizSprited {
    File quizSprited;
    String quizHtml;
    public CreateQuizSprited(File quizSprited, String quizHtml) {
        this.quizSprited = quizSprited;
        this.quizHtml = quizHtml;
    }
    public String createQuizSpritedHtml() {
        return "";
    }
}
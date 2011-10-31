package hotmath.gwt.cm_admin.server.model;

import java.util.ArrayList;
import java.util.List;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.CustomQuizId;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.gwt.shared.client.rpc.action.SaveCustomQuizAction;
import hotmath.gwt.shared.server.service.command.SaveCustomQuizCommand;


public class CmQuizzesDao_Test extends CmDbTestCase {

    public CmQuizzesDao_Test(String name) {
        super(name);
    }
    
    public void testGetQuestions() throws Exception {
        String lesson= "topics/adding-and-subtracting-decimals.html";
        CmList<QuizQuestion> questions = CmQuizzesDao.getInstance().getQuestionsFor(conn, lesson, "pre-alg");
        assertTrue(questions.get(0).getQuizHtml().length() > 0);
        assertTrue(questions.get(0).getCorrectAnswer() > 0);
    }
    
    public void testGetQuizDef() throws Exception {
    	CustomQuizDef qd = CmQuizzesDao.getInstance().getCustomQuiz(87);
    	assertTrue(qd != null);
    }
    
    public void testSaveLoad() throws Exception {
        
        /** save one, then load it back
         * 
         */
        int adminId = 2;
        String cpName = "Custom Quiz: " + System.currentTimeMillis();
        List<CustomQuizId> ids = new ArrayList<CustomQuizId>();
        ids.add(new CustomQuizId("prealgptests2_CourseTest_1_Pre-AlgebraPracticeTest_10_1",0));
        ids.add(new CustomQuizId("prealgptests2_CourseTest_1_Pre-AlgebraPracticeTest_2_1",1));
        SaveCustomQuizAction action = new SaveCustomQuizAction(adminId, cpName, ids);
        
        RpcData data = new SaveCustomQuizCommand().execute(conn, action);
        assertTrue(data.getDataAsInt("custom_quiz_id") > 0);
        assertTrue(data != null && data.getDataAsString("status").equals("OK"));
        
        int customQuizId = data.getDataAsInt("custom_quiz_id");
        
        CmList<QuizQuestion> questions = CmQuizzesDao.getInstance().getCustomQuizQuestions(conn,customQuizId);
        assertTrue(questions.size() > 0);
        
        assertTrue(CmQuizzesDao.getInstance().deleteCustomQuiz(conn, adminId, cpName));
    }
}

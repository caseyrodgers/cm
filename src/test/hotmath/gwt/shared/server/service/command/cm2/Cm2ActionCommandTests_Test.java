package hotmath.gwt.shared.server.service.command.cm2;

import hotmath.gwt.cm_rpc.client.rpc.cm2.CheckCm2QuizAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.GetCm2QuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.QuizCm2CheckedResult;
import hotmath.gwt.cm_rpc.client.rpc.cm2.QuizCm2HtmlResult;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.testset.ha.HaTestDao;
import junit.framework.TestCase;

public class Cm2ActionCommandTests_Test extends TestCase{
    
    public Cm2ActionCommandTests_Test(String name) {
        super(name);
    }
    
    // int TEST_ID=1928132;
    int TEST_ID=2610241;
    public void testGetQuizActionDispatcher() throws Exception {
        GetCm2QuizHtmlAction action = new GetCm2QuizHtmlAction(TEST_ID);
        QuizCm2HtmlResult results = ActionDispatcher.getInstance().execute(action);
        assertTrue(results != null);
    }
    
    int USER_ID=678563;
    public void testCheckQuizActionDispatcher() throws Exception {
        
        HaTestDao.resetTest(TEST_ID);
        
        CheckCm2QuizAction action = new CheckCm2QuizAction(TEST_ID);
        QuizCm2CheckedResult results = ActionDispatcher.getInstance().execute(action);
        assertTrue(results != null);
    }


}

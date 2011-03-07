package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.gwt.shared.client.rpc.action.GetLessonQuestionsAction;

public class GetLessonQuestionsCommand_Test extends CmDbTestCase {
    
    public GetLessonQuestionsCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        GetLessonQuestionsAction action = new GetLessonQuestionsAction("topics/venn-diagrams.html","alg1");
        CmList<QuizQuestion> questions = new GetLessonQuestionsCommand().execute(conn, action);
        assertTrue(questions.size() > 0);
    }

}

package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;

public class GetReviewHtmlCommand_Test extends CmDbTestCase {

    static String TEST_FILE = "topics/angles.html";

    public GetReviewHtmlCommand_Test(String name) {
        super(name);
    }

    public void testGetReviewHtmlCommand() throws Exception {
        GetReviewHtmlAction action = new GetReviewHtmlAction(TEST_FILE);
        LessonResult lesson = ActionDispatcher.getInstance().execute(action);
        assertNotNull(lesson.getLesson());
        assertTrue(lesson.isHasSpanish());
    }

    public void testGetReviewHtmlCommandSpanish() throws Exception {
        GetReviewHtmlAction action = new GetReviewHtmlAction(TEST_FILE);
        action.setSpanish(true);
        LessonResult st = new GetReviewHtmlCommand().execute(conn, action);
        assertNotNull(st.getLesson());
        assertTrue(st.getLesson().indexOf("ngulos") > -1);
    }

}

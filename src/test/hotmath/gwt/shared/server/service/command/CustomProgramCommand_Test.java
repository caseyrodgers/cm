package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction.ActionType;
import hotmath.gwt.shared.server.service.ActionDispatcher;


public class CustomProgramCommand_Test extends CmDbTestCase {
    
    public CustomProgramCommand_Test(String name) {
        super(name);
    }
    
    public void testGetLessons() throws Exception {
        CustomProgramAction action = new CustomProgramAction(ActionType.GET_ALL_LESSONS);
        CmList<CustomLessonModel> lessons = new CustomProgramCommand().execute(conn,action);
        assertTrue(lessons.size() > 0);
    }
    
    public void testGetLessonsDispatch() throws Exception {
        CustomProgramAction action = new CustomProgramAction(ActionType.GET_ALL_LESSONS);
        CmList<CustomLessonModel> lessons = ActionDispatcher.getInstance().execute(action);
        assertTrue(lessons.size() > 0);
    }    
}

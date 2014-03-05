package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction.ActionType;


public class CustomProgramCommand_Test extends CmDbTestCase {
    
    public CustomProgramCommand_Test(String name) {
        super(name);
    }
    
    public void testCopyCustomProblem() throws Exception {
        CustomProgramAction action = new CustomProgramAction(ActionType.COPY);
        action.setAdminId(5);
        action.setProgramId(328);
        action.setDestAdminId(2);
        
        CmList<CustomLessonModel> lessons = new CustomProgramCommand().execute(conn,action);
        assertTrue(lessons.size() != 0);
    }
    public void testGetLessons() throws Exception {
        CustomProgramAction action = new CustomProgramAction(ActionType.GET_ALL_LESSONS);
        CmList<CustomLessonModel> lessons = new CustomProgramCommand().execute(conn,action);
        assertTrue(lessons.size() > 0);
    }
}

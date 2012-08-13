package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.AssignmentLessonData;
import hotmath.gwt.cm_rpc.client.rpc.AssignmentGetLessonsAction;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;

public class AssignmentGetLessonsCommand_Test extends TestCase {
    
    public AssignmentGetLessonsCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        AssignmentGetLessonsAction action = new AssignmentGetLessonsAction(); 
        AssignmentLessonData data = new AssignmentGetLessonsCommand().execute(HMConnectionPool.getConnection(), action);
        assertTrue(data.getSubjects().size() > 0);
    }

}

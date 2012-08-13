package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonProblemsAction;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;


public class GetProgramLessonProblemsCommand_Test extends TestCase {
    public GetProgramLessonProblemsCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        GetProgramLessonProblemsAction action = new GetProgramLessonProblemsAction("Square", "Ess");
        CmList<ProblemDto> data = new GetProgramLessonProblemsCommand().execute(HMConnectionPool.getConnection(),action);
        assertTrue(data.size() > 0);
    }
}

package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.GetProgramLessonProblemsAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.util.HMConnectionPool;
import junit.framework.TestCase;


public class GetProgramLessonProblemsCommand_Test extends TestCase {
    public GetProgramLessonProblemsCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        GetProgramLessonProblemsAction action = new GetProgramLessonProblemsAction(2, "Absolute Value", "topics/absolute-value.html","Ess");
        CmList<ProblemDto> data = new GetProgramLessonProblemsCommand().execute(HMConnectionPool.getConnection(),action);
        assertTrue(data.size() > 0);
    }
}

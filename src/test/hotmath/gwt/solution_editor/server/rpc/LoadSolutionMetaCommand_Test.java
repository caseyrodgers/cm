package hotmath.gwt.solution_editor.server.rpc;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.solution_editor.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionMeta;

public class LoadSolutionMetaCommand_Test extends CmDbTestCase {
    
    public LoadSolutionMetaCommand_Test(String name) {
        super(name);
    }
    
    public void testGet() throws Exception {
        String pid = "cmextrasalg1_1_1_1_1_1";
        LoadSolutionMetaAction action = new LoadSolutionMetaAction(pid);
        SolutionMeta solution = new LoadSolutionMetaCommand().execute(conn, action);
        assertTrue(solution.getPid().equals(pid));
    }

}

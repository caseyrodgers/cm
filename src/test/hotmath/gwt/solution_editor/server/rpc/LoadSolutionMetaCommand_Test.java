package hotmath.gwt.solution_editor.server.rpc;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.solution_editor.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionMeta;

public class LoadSolutionMetaCommand_Test extends CmDbTestCase {
    
    public LoadSolutionMetaCommand_Test(String name) {
        super(name);
    }
    
    String pidThatExists = "cmextrasalg1_1_1_1_1_1";
    
    public void testGet() throws Exception {
        LoadSolutionMetaAction action = new LoadSolutionMetaAction(pidThatExists);
        SolutionMeta solution = new LoadSolutionMetaCommand().execute(conn, action);
        assertTrue(solution.getPid().equals(pidThatExists));
    }
    
    public void testGetMd5() throws Exception {
     
        LoadSolutionMetaAction action = new LoadSolutionMetaAction(pidThatExists);
        SolutionMeta solution1 = new LoadSolutionMetaCommand().execute(conn, action);
        
        SolutionMeta solution2 = new LoadSolutionMetaCommand().execute(conn, action);
        assertTrue(solution1.getMd5OnRead() != null && solution1.getMd5OnRead().equals(solution2.getMd5OnRead()));
    }    

}

package hotmath.gwt.solution_editor.server;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.solution_editor.client.SolutionSearchModel;
import hotmath.solution.Solution;

public class CmSolutionManagerDao_Test extends CmDbTestCase {
    
    public CmSolutionManagerDao_Test(String name) {
        super(name);
    }
    
    public void testSearchFor() throws Exception {
        CmList<SolutionSearchModel> list = new CmSolutionManagerDao().searchForSolutions(conn, "test_");
        assertTrue(list.size() > 0);
    }
    
    public void testCreateNewSolution() throws Exception {
        String newPid = new CmSolutionManagerDao().createNewSolution(conn);
        Solution solution = hotmath.SolutionManager.getSolution(newPid);
        String ng = solution.getGUID();
        assertTrue(solution.getGUID().equals(newPid));
    }
}

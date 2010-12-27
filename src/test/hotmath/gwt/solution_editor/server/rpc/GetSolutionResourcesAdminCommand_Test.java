package hotmath.gwt.solution_editor.server.rpc;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;
import hotmath.gwt.solution_editor.client.rpc.GetSolutionResourcesAdminAction.ResourceType;

public class GetSolutionResourcesAdminCommand_Test extends CmDbTestCase {
    public GetSolutionResourcesAdminCommand_Test(String name) {
        super(name);
    }
    
    public void testGetLocal() throws Exception {
        GetSolutionResourcesAdminAction action = new GetSolutionResourcesAdminAction("test_pid_1_1",ResourceType.LOCAL);
        CmList<SolutionResource> resources = new GetSolutionResourcesAdminCommand().execute(conn, action);
        assertTrue(resources.size() > 0);
    }
    
    public void testGetGlobal() throws Exception {
        GetSolutionResourcesAdminAction action = new GetSolutionResourcesAdminAction("test_pid_1_1",ResourceType.GLOBAL);
        CmList<SolutionResource> resources = new GetSolutionResourcesAdminCommand().execute(conn, action);
        assertTrue(resources.size() > 0);
    }


}

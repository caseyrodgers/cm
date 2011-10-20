package hotmath.gwt.shared.server.service;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.ParallelProgramModel;
import hotmath.gwt.shared.client.rpc.action.GetParallelProgramsAction;

public class GetParellelProgramsCommand_Test extends CmDbTestCase {
    

    public GetParellelProgramsCommand_Test(String name) {
        super(name);
    }
    
    static final int TEST_AID = 6;  // known to have parallel programs
   
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testRead() throws Exception {
        GetParallelProgramsAction action = new GetParallelProgramsAction(TEST_AID);
        CmList<ParallelProgramModel> ppList = ActionDispatcher.getInstance().execute(action);
        
        assertTrue(ppList.size() > 0);
    }
}

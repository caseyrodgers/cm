package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetActiveInfoForStudentUidAction;

public class GetActiveInfoForStudentUidCommand_Test extends CmDbTestCase {
    public GetActiveInfoForStudentUidCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_test == null)
            setupDemoAccountTest();
    }
    
    public void testGetActiveInfo() throws Exception {
    	GetActiveInfoForStudentUidAction action = new GetActiveInfoForStudentUidAction();
    	action.setUserId(_test.getUser().getUid());
    	
    	GetActiveInfoForStudentUidCommand cmd = new GetActiveInfoForStudentUidCommand();
    	StudentActiveInfo mdl = cmd.execute(conn, action);
    	
    	assertNotNull(mdl);
    	assertTrue(mdl.getActiveSegment()>0);
    	assertTrue(mdl.getActiveProgram().getSegmentCount() > 0);
    }
    
}

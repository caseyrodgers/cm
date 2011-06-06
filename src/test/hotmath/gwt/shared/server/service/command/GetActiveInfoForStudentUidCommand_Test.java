package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.GetActiveInfoForStudentUidAction;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfoModel;

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
    	StudentActiveInfoModel mdl = cmd.execute(conn, action);
    	
    	assert(mdl != null && mdl.getActiveSegment() >= 0); 
    }
    
}

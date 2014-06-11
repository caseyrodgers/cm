package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentHTMLAction;

public class GetAssignmentHTMLCommand_Test extends CmDbTestCase {

    public GetAssignmentHTMLCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_test == null)
            setupDemoAccountTest();
    }
    
    public void testGetAssignnentHTML() throws Exception {
    	GetAssignmentHTMLAction action = new GetAssignmentHTMLAction();
    	action.setAssignKey(3);
    	
    	GetAssignmentHTMLCommand cmd = new GetAssignmentHTMLCommand();
    	String html = cmd.execute(conn, action).getHtml();
    	
    	assert(html != null && html.trim().length() > 0);
    }
    
}

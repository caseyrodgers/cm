package hotmath.gwt.solution_editor.server.rpc;

import hotmath.gwt.cm_rpc.client.model.SolutionAdminResponse;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.solution_editor.client.rpc.FormatXmlAdminAction;
import junit.framework.TestCase;

public class FormatXmlAdminCommand_Test extends TestCase {
    
    public FormatXmlAdminCommand_Test(String name) {
        super(name);
    }
    
    public void testFormat1() throws Exception {
        String testXml="<test></test>";        
        FormatXmlAdminAction action = new FormatXmlAdminAction(testXml);
        SolutionAdminResponse response = ActionDispatcher.getInstance().execute(action);
        assertTrue(response.getXml().equals("<test/>"));
    }

}

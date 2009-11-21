package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.shared.client.rpc.action.GetCmVersionInfoAction;
import hotmath.gwt.shared.client.rpc.result.CmVersionInfo;
import junit.framework.TestCase;

public class GetCmVersionInfoCommand_Test extends TestCase {
    
    public GetCmVersionInfoCommand_Test(String name) {
        super(name);
    }
    
    
    public void testGetVersion() throws Exception {
        GetCmVersionInfoAction action = new GetCmVersionInfoAction("http://localhost:8088");
        CmVersionInfo version = new GetCmVersionInfoCommand().execute(null, action);
        assertTrue(version.getGwtBuildVersion() > 0);
    }

}

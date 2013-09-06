package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSStrandCoverage;
import hotmath.gwt.shared.client.rpc.action.CCSSStrandCoverageAction;

public class CCSSStrandCoverageCommand_Test extends CmDbTestCase {
    
    public CCSSStrandCoverageCommand_Test(String name) {
        super(name);
    }

    public void testStrandCoverage() throws Exception {
    	CCSSStrandCoverageAction action = new CCSSStrandCoverageAction(2, 23472, null, null);
        CmList<CCSSStrandCoverage> list = new CCSSStrandCoverageCommand().execute(conn, action);
        assertTrue(list.size() > 0);
    }

}

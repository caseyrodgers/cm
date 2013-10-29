package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CCSSStrandCoverageData;
import hotmath.gwt.shared.client.rpc.action.CCSSStrandCoverageDataAction;

public class CCSSStrandCoverageDataCommand_Test extends CmDbTestCase {
    
    public CCSSStrandCoverageDataCommand_Test(String name) {
        super(name);
    }

    public void testStrandCoverage() throws Exception {
    	CCSSStrandCoverageDataAction action = new CCSSStrandCoverageDataAction(2, 23472, null, null);
    	action.setLevelName("Grade 8");
        CmList<CCSSStrandCoverageData> list = new CCSSStrandCoverageDataCommand().execute(conn, action);
        assertTrue(list.size() > 0);
    }

}

package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.StateStandard;
import hotmath.gwt.shared.client.rpc.action.GetStateStandardsAction;

public class GetStateStandardsCommand_Test extends CmDbTestCase {
    
    public GetStateStandardsCommand_Test(String name) {
        super(name);
    }
    
    GetStateStandardsAction action;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        action = new GetStateStandardsAction();
        action.setTopic("absolute-value.html");        
    }
    public void testGetCalStandards() throws Exception {
        action.setState("common");
        CmList<StateStandard> standards = new GetStateStandardsCommand().execute(conn, action);
        assertTrue(standards.size() > 0);
    }
    
    public void testGetTxStandards() throws Exception {
        action.setState("TX");
        CmList<StateStandard> standards = new GetStateStandardsCommand().execute(conn, action);
        assertTrue(standards.size() > 0);
    }

    public void testGetUtStandards() throws Exception {
        action.setState("UT");
        CmList<StateStandard> standards = new GetStateStandardsCommand().execute(conn, action);
        assertTrue(standards.size() > 0);
    }
}

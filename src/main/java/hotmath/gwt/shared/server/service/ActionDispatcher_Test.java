package hotmath.gwt.shared.server.service;

import hotmath.gwt.shared.client.rpc.action.GetPrescriptionAction;
import hotmath.gwt.shared.client.rpc.action.GetSolutionAction;
import hotmath.gwt.shared.client.rpc.action.GetViewedInmhItemsAction;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.List;

import junit.framework.TestCase;

public class ActionDispatcher_Test extends TestCase {
    
    /** @TODO: create temp values for testing
     * 
     */
    static final int TEST_RUN_ID=13351;
    static final int TEST_UID=699;
    static final String TEST_PID="genericalg1_2_4_GraphingLinearEquations_5_115";
    
    public ActionDispatcher_Test(String name) {
        super(name);
    }

    
    
    public void testCreateGetInmhViewsForRun() throws Exception {
        
        GetViewedInmhItemsAction action = new GetViewedInmhItemsAction(TEST_RUN_ID);
        List<RpcData> data = ActionDispatcher.getInstance().execute(action).getRpcData();
        
        assertNotNull(data);
        assertTrue(data.size() > 0);
    }

    
    public void testCreatePrescription() throws Exception {
        
        GetPrescriptionAction action = new GetPrescriptionAction(TEST_RUN_ID,0,false);
        RpcData data = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(data);
        assertNotNull(data.getData("program_title"));
    }
  
    public void testGetSolution() throws Exception {
        
        GetSolutionAction action = new GetSolutionAction(TEST_UID, TEST_PID);
        RpcData data = ActionDispatcher.getInstance().execute(action);
        
        assertNotNull(data);
        assertNotNull(data.getData("solutionHtml"));
        assertNotNull(data.getData("hasShowWork"));
    }
}

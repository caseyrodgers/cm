package hotmath.gwt.shared.server.service.command;

import org.junit.Test;

import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;

public class GetCmProgramFlowCommand_Test extends CmDbTestCase {
    public GetCmProgramFlowCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_test == null)
            setupDemoAccountTest();
    }
    
    public void testCreateFlow() throws Exception {
        CmProgramFlow flow = new CmProgramFlow(conn, _test.getUser().getUserKey());
        CmProgramFlowAction nextAction = flow.getActiveFlowAction(conn);
        assertTrue(nextAction.getPlace() == CmPlace.QUIZ);
    }
    
    public void testCreateFlowAdvance() throws Exception {
        CmProgramFlow cmProgram = new CmProgramFlow(conn, _test.getUser().getUserKey());
        
        int seg = cmProgram.getActiveTestSegment();
        
        cmProgram.moveToNextProgramSegment(conn);
        CmProgramFlowAction nextAction = cmProgram.getActiveFlowAction(conn);
        assertTrue(nextAction.getPlace() == CmPlace.QUIZ);
        
        assertTrue(seg+1 == cmProgram.getActiveTestSegment());
        
    }
    
    @Test
    public void setActiveSegment() throws Exception {
    	CmProgramFlow flow = new CmProgramFlow(conn, _test.getUser().getUid());

    	flow.getActiveInfo().setActiveSegment(2);
    	flow.saveActiveInfo(conn);

    	flow = new CmProgramFlow(conn, _test.getUser().getUid());
    	assertTrue(flow.getActiveInfo().getActiveSegment() == 2);
    }
}

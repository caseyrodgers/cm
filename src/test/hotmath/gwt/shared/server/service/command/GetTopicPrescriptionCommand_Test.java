package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import junit.framework.TestCase;

public class GetTopicPrescriptionCommand_Test extends TestCase {
    
    public GetTopicPrescriptionCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        GetTopicPrescriptionAction action = new GetTopicPrescriptionAction("topics/integers.html");
        PrescriptionSessionResponse data = ActionDispatcher.getInstance().execute(action);
        assertNotNull(data);
        assertTrue(data.getRunId() > 0);
        assertTrue(data.getPrescriptionData().getCurrSession().getInmhResources().size() > 0);
    }

}

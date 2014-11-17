package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.DbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;

public class GetTopicPrescriptionCommand_Test extends DbTestCase {
    
    public GetTopicPrescriptionCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        GetTopicPrescriptionAction action = new GetTopicPrescriptionAction("topics/integers.html");
        PrescriptionSessionResponse data = new GetTopicPrescriptionCommand().execute(conn, action);
        assertNotNull(data);
        assertTrue(data.getRunId() > 0);
        assertTrue(data.getPrescriptionData().getCurrSession().getInmhResources().size() > 0);
    }

}

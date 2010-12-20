package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;

import java.util.List;

public class GetPrescriptionCommand_Test extends CmDbTestCase{
    
    public GetPrescriptionCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_testRun == null)
            setupDemoAccountTestRun();
    }
    
    public void testOne() throws Exception {
        GetPrescriptionAction action = new GetPrescriptionAction(_testRun.getRunId(),0, true);
        PrescriptionSessionResponse response = new GetPrescriptionCommand().execute(conn, action);
        PrescriptionSessionData psd = response.getPrescriptionData().getCurrSession();
        
        List<PrescriptionSessionDataResource> resources = psd.getInmhResources();
        
        assertTrue(resources.size() > 0);
    }

}

package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class GeneratePdfCommand_Test extends CmDbTestCase {
    
    public GeneratePdfCommand_Test(String name) throws Exception {
        super(name);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_test == null)
            setupDemoAccountTest();
    }
    
    public void testCreateTestReport() throws Exception {
        List<Integer> studentUids = Arrays.asList(_user.getUid());  

        GeneratePdfAction action = new GeneratePdfAction(PdfType.REPORT_CARD,_user.getAid(),studentUids);
        CmWebResource resource = new GeneratePdfCommand().execute(conn, action);
        assertTrue(new File(resource.getFile()).exists());
        assertTrue(resource.getUrl() != null);
    }

}

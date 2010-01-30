package hotmath.gwt.shared.server.service;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAssessmentReportAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;
import hotmath.gwt.shared.server.service.command.GeneratePdfAssessmentReportCommand;

import java.net.URL;

public class GeneratePdfAssessmentReportCommand_Test extends CmDbTestCase {
    
    public GeneratePdfAssessmentReportCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        CmWebResourceManager.setFileBase("/temp");
    }
    
    public void testCreate() throws Exception {
        GetStudentGridPageAction pageAction = new GetStudentGridPageAction();
        pageAction.setAdminId(2);
        CmWebResource wResource = new GeneratePdfAssessmentReportCommand().execute(conn, new GeneratePdfAssessmentReportAction(2, pageAction));
        assertTrue(wResource.getFile() != null);
        assertTrue(new URL(wResource.getUrl()).getHost() != null);
    }
}

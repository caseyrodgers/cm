package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;

import java.net.URL;

public class ExportStudentsCommand_Test extends CmDbTestCase {
    
    public ExportStudentsCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        CmWebResourceManager.setFileBase("/temp");
    }
    
    public void testCreate() throws Exception {
        GetStudentGridPageAction pageAction = new GetStudentGridPageAction();
        pageAction.setAdminId(71);
        CmWebResource wResource = new ExportStudentsCommand().execute(conn, new ExportStudentsAction(71, pageAction));
        assertTrue(wResource.getFile() != null);
        //assertTrue(new URL(wResource.getUrl()).getHost() != null);
    }
}

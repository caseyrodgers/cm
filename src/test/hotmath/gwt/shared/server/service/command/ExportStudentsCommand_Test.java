package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;

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

        ExportStudentsAction exportAction = new ExportStudentsAction(71, pageAction);
        exportAction.setEmailAddress("bobhall@hotmath.com");
        StringHolder sh = new ExportStudentsCommand().execute(conn, exportAction);
        assertTrue(sh != null);
        //assertTrue(new URL(wResource.getUrl()).getHost() != null);
    }
}

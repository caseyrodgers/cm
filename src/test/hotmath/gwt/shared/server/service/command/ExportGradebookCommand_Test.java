package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.rpc.action.ExportGradebookAction;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;

public class ExportGradebookCommand_Test extends CmDbTestCase {
    
    public ExportGradebookCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        CmWebResourceManager.setFileBase("/temp");
    }
    
    public void testCreate() throws Exception {
        GetStudentGridPageAction pageAction = new GetStudentGridPageAction();
        pageAction.setAdminId(584);

        ExportGradebookAction exportAction = new ExportGradebookAction(584, 2281, null, pageAction);
        exportAction.setEmailAddress("bobhall@hotmath.com");
        ExportGradebookCommand cmd = new ExportGradebookCommand();
        cmd.setRunInSeparateThread(false);
        StringHolder sh = cmd.execute(conn, exportAction);
        assertTrue(sh != null);
        //assertTrue(new URL(wResource.getUrl()).getHost() != null);
    }
}

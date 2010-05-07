package hotmath.gwt.shared.server.service.command.program_listing;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramListingAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.server.service.command.GetProgramListingCommand;

public class GetProgramListingCommand_Test extends CmDbTestCase {
    
    public GetProgramListingCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        GetProgramListingAction action = new GetProgramListingAction();
        action.setAdminId(2);
        ProgramListing pr = new GetProgramListingCommand().execute(conn, action);
        assertTrue(pr.getProgramTypes().size() > 0);
    }

    public void testCreateRpc() throws Exception {
        GetProgramListingAction action = new GetProgramListingAction();
        action.setAdminId(2);
        ProgramListing pr = ActionDispatcher.getInstance().execute(action);
        assertTrue(pr.getProgramTypes().size() > 0);
    }
    
    public void testCreateLessons() throws Exception {
        GetProgramListingAction action = new GetProgramListingAction();
        action.setAdminId(2);
        ProgramListing pr = ActionDispatcher.getInstance().execute(action);
        assertTrue(pr.getProgramTypes().get(0).getProgramSubjects().get(0).getChapters().get(0).getLessons().size() > 0);
    }
}

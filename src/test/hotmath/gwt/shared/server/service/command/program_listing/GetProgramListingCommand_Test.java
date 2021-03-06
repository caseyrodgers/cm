package hotmath.gwt.shared.server.service.command.program_listing;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.rpc.GetProgramListingAction;
import hotmath.gwt.shared.server.service.command.GetProgramListingCommand;

public class GetProgramListingCommand_Test extends CmDbTestCase {
    
    public GetProgramListingCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        GetProgramListingAction action = new GetProgramListingAction();
        action.setAdminId(2);
        ProgramListing pr = new GetProgramListingCommand().execute(conn, action);
        assertTrue(pr.getProgramTypes().get(0).getLabel().contains("Proficiency"));
    }
}

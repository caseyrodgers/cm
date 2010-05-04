package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmProgramListingDao;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc.server.rpc.program_listing.GetProgramListingAction;

import java.sql.Connection;

/** Provide high level access to program listing data
 * 
 * @author casey
 *
 */
public class GetProgramListingCommand implements ActionHandler<GetProgramListingAction, ProgramListing>{

    @Override
    public ProgramListing execute(Connection conn, GetProgramListingAction action) throws Exception {
        ProgramListing pr = new CmProgramListingDao().getProgramListing(conn);
        return pr;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetProgramListingAction.class;
    }
}

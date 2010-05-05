package hotmath.gwt.cm_rpc.server.rpc.program_listing;

import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.rpc.Action;

public class GetProgramListingAction implements Action<ProgramListing> {
	
	int adminId;
	
    public GetProgramListingAction(){}
    
    public GetProgramListingAction(Integer adminId) {
        this.adminId = adminId;
    }
    
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    @Override
    public String toString() {
        return "GetProgramListingAction [adminId=" + adminId + "]";
    }

}

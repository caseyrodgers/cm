package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class GetProgramListingAction implements Action<ProgramListing> {
	
	private static final long serialVersionUID = 5403381683014060964L;

	int adminId;
	boolean includeBuiltInCustomProgs;
	
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

    public boolean isIncludeBuiltInCustomProgs() {
		return includeBuiltInCustomProgs;
	}

	public void setIncludeBuiltInCustomProgs(boolean includeBuiltInCustomProgs) {
		this.includeBuiltInCustomProgs = includeBuiltInCustomProgs;
	}

	@Override
    public String toString() {
        return "GetProgramListingAction [aid=" + adminId + ", includeBuiltInCustomProgs=" + includeBuiltInCustomProgs + "]";
    }

}

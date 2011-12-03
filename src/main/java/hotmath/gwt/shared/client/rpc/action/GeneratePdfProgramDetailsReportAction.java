package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.CmWebResource;

public class GeneratePdfProgramDetailsReportAction implements Action<CmWebResource>{

	Integer adminId;
	ProgramListing programListing;

	public GeneratePdfProgramDetailsReportAction() {
	}

	public GeneratePdfProgramDetailsReportAction(Integer adminId, ProgramListing programListing) {
        this.adminId = adminId;
        this.programListing = programListing;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public ProgramListing getProgramListing() {
		return programListing;
	}

	public void setProgramListing(ProgramListing programListing) {
		this.programListing = programListing;
	}

	@Override
    public String toString() {
        return "GeneratePdfProgramDetailsReportAction [adminId: " + adminId + ", programLIsting: " + programListing + "]";
    }
}

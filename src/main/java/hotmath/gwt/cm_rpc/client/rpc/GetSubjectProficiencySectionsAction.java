package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSection;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetSubjectProficiencySectionsAction implements Action<CmList<ProgramSection>> {

	private static final long serialVersionUID = 766971331390982967L;
	
    private String subject;

    public GetSubjectProficiencySectionsAction(){}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}

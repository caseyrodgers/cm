package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.ChapterModel;

public class GetChaptersForProgramSubjectAction implements Action<CmList<ChapterModel>>{
	
	private static final long serialVersionUID = 3338241720554819147L;

	String progId;
	String subjId;
	
	public GetChaptersForProgramSubjectAction() {}
	
	public GetChaptersForProgramSubjectAction(String progId, String subjId) {
		this.progId = progId;
		this.subjId = subjId;
	}

	public String getProgId() {
		return progId;
	}

	public void setProgId(String progId) {
		this.progId = progId;
	}

	public String getSubjId() {
		return subjId;
	}

	public void setSubjId(String subjId) {
		this.subjId = subjId;
	}
}

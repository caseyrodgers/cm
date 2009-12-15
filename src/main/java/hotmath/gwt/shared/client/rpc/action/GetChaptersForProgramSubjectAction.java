package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.shared.client.rpc.Action;

public class GetChaptersForProgramSubjectAction implements Action<CmList<ChapterModel>>{
	
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

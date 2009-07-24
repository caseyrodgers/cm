package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.shared.client.rpc.Response;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class SubjectModel extends BaseModelData implements Response {

	private static final long serialVersionUID = 5518799370079789930L;
	
	public SubjectModel() {
		;
	}

	public SubjectModel(String title, String abbrev) {
		set("subject", title);
		set("abbrev", abbrev);
	}
	
	public void setTitle(String title) {
		set("subject", title);
	}
	
	public void setAbbrev(String abbrev) {
		set("abbrev", abbrev);
	}

	public String getTitle() {
		return get("subject");
	}
	
	public String getAbbrev() {
		return get("abbrev");
	}

}

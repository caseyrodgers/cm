package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SubjectModel extends BaseModel implements Response, IsSerializable {

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

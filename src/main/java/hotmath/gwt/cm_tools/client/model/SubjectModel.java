package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class SubjectModel implements Response{

	
	String subject, abbrev;
	String styleIsFree;
	
	public SubjectModel() {}

	public SubjectModel(String title, String abbrev) {
		this.subject = title;
		this.abbrev = abbrev;
	}

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getStyleIsFree() {
        return styleIsFree;
    }

    public void setStyleIsFree(String styleIsFree) {
        this.styleIsFree = styleIsFree;
    }
}

package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CCSSDetail implements Response {

	private static final long serialVersionUID = -256657099916473784L;

	String ccssName;
    String ccssOriginalName;
    String gradeLevel;
    String topic;
    String summary;
    String description;
    
    public CCSSDetail(String ccssName, String ccssOriginalName, String gradeLevel, String topic, String summary, String description) {
        this.ccssName = ccssName;
        this.ccssOriginalName = ccssOriginalName;
        this.gradeLevel = gradeLevel;
        this.topic = topic;
        this.summary = summary;
        this.description = description;
    }

	public String getCcssName() {
		return ccssName;
	}

	public void setCcssName(String ccssName) {
		this.ccssName = ccssName;
	}

	public String getCcssOriginalName() {
		return ccssOriginalName;
	}

	public void setCcssOriginalName(String ccssOriginalName) {
		this.ccssOriginalName = ccssOriginalName;
	}

	public String getGradeLevel() {
		return gradeLevel;
	}

	public void setGradeLevel(String gradeLevel) {
		this.gradeLevel = gradeLevel;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}

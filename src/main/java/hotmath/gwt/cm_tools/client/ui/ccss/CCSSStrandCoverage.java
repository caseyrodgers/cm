package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CCSSStrandCoverage implements Response {

	private static final long serialVersionUID = 7185800353431491189L;

	int count;
	String levelName;
	String label;
	String percentComplete;

	public CCSSStrandCoverage() {
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(String percentComplete) {
		this.percentComplete = percentComplete;
	}

}

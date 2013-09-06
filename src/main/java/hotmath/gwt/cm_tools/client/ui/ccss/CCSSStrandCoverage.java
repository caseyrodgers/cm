package hotmath.gwt.cm_tools.client.ui.ccss;

public class CCSSStrandCoverage extends CCSSCoverageReport {

	String percentComplete;
	int count;

	public CCSSStrandCoverage() {
	}

	public CCSSStrandCoverage(CCSSCoverageImplBase report) {
		super(report);
	}

	public String getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(String percentComplete) {
		this.percentComplete = percentComplete;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}

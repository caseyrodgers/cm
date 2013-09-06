package hotmath.gwt.cm_tools.client.ui.ccss;

public class CCSSCoverageReport {

	CCSSCoverageImplBase report;
	String decorationClass;
	String label;
	boolean isGroupReport;

	public CCSSCoverageReport() {
		// empty
	}

	public CCSSCoverageReport(CCSSCoverageImplBase report) {
		this.report = report;
	}

	/** The report name 
	 * 
	 * @return
	 */
	public String getText() {
		return report.getText();
	}

	public void setLabel(String label) {
	    this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getToolTip() {
		return report.getToolTip();
	}

	public CCSSCoverageImplBase getReport() {
		return report;
	}

	public String getDecorationClass() {
		return decorationClass;
	}

	public void setDecorationClass(String decorationClass) {
		this.decorationClass = decorationClass;
	}

	public boolean isGroupReport() {
		return isGroupReport;
	}

	public void setGroupReport(boolean isGroupReport) {
		this.isGroupReport = isGroupReport;
	}


}

package hotmath.gwt.cm_tools.client.ui.ccss;

public class CCSSCoverageReport {

	CCSSCoverageImplBase report;
	String decorationClass;
	boolean isGroupReport;
	
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

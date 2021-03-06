package hotmath.gwt.cm_admin.client.ui.highlights;


public class HighlightsReport {

	HighlightsImplBase report;
	String decorationClass;
	boolean isGroupReport;
	
	    public HighlightsReport(HighlightsImplBase report) {
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
	    
	    public HighlightsImplBase getReport() {
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

package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplLeastEffort extends HighlightsImplBase {
    HighlightsImplLeastEffortDetailsPanel panel = new HighlightsImplLeastEffortDetailsPanel(this);
    static String title = "Least Effort";
    
    public HighlightsImplLeastEffort() {
        super(title, "Displays students in order of least lessons viewed (excluding those who have viewed zero lessons).");
    }
    public Widget prepareWidget() {
        return panel;
    }
    @Override
    protected String[][] getReportValues() {
        return panel.getReportValues();
    }
    @Override
    protected HighlightReportLayout getReportLayout() {
    	HighlightReportLayout rl = super.getReportLayout();
    	rl.setTitle(title);
    	return rl;
    }
}

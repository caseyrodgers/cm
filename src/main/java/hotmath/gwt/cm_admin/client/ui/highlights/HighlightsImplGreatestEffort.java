package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplGreatestEffort extends HighlightsImplBase {

    HighlightsImplGreatestEffortDetailsPanel panel = new HighlightsImplGreatestEffortDetailsPanel(this);

    static String title = "Greatest Effort";

    public HighlightsImplGreatestEffort() {
        super(title, "Displays students in order of most lessons viewed (excluding those who have viewed zero lessons).");
    }
    
    public Widget prepareWidget() {
        return panel;
    }

    @Override
    protected String[][] getReportValues() {
        return panel.getReportValues();
    }
    
    @Override
    String[] getReportCols() {
        return panel.getReportColumns();
    }

    @Override
    protected HighlightReportLayout getReportLayout() {
    	HighlightReportLayout rl = super.getReportLayout();
    	rl.setTitle(title);
    	return rl;
    }
    
}

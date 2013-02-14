package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_admin.client.ui.highlights.HighlightsImplGreatestEffortData;
import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplGreatestEffort extends HighlightsImplBase {

    HighlightsImplGreatestEffortData data = new HighlightsImplGreatestEffortData(
			new HighlightsImplGreatestEffortDetailsPanel(this));

	static String title = "Greatest Effort";

    public HighlightsImplGreatestEffort() {
        super(title, "Displays students in order of most lessons viewed (excluding those who have viewed zero lessons).");
    }
    
    public Widget prepareWidget() {
        return data.panel;
    }
    
    @Override
    protected HighlightReportLayout getReportLayout() {
    	HighlightReportLayout rl = super.getReportLayout();
    	rl.setTitle(title);
    	return rl;
    }

//    @Override
//    protected String[][] getReportValues() {
//        return data.panel.getReportValues();
//    }
}

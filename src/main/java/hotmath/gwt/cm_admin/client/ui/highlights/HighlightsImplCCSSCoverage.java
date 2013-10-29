package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplCCSSCoverage extends HighlightsImplBase {
    HighlightsImplCCSSCoveragePanel panel = new HighlightsImplCCSSCoveragePanel(this);
    static String title = "CCSS Coverage";
    
    public HighlightsImplCCSSCoverage() {
        super(title,"Displays CCSS standards and the number of students that have covered them.");
    }

    public Widget prepareWidget() {
        return panel;
    }
    
    @Override
    String[] getReportCols() {
        return panel.getReportColumns();
    }
    
    @Override
    String[][] getReportValues() {
        return panel.getReportValues();
    }

    @Override
    protected HighlightReportLayout getReportLayout() {
    	HighlightReportLayout rl = super.getReportLayout();
    	rl.setTitle(title);
    	return rl;
    }
    
}

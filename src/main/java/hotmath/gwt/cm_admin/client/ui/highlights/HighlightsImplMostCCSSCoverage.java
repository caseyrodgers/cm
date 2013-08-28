package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

public class HighlightsImplMostCCSSCoverage extends HighlightsImplBase {
    HighlightsImplMostCCSSCoveragePanel panel = new HighlightsImplMostCCSSCoveragePanel(this);
    static String title = "Most CCSS Coverage";
    public HighlightsImplMostCCSSCoverage() {
        super(title, "Displays students in order of most CCSS coverage.");
    }
    public HighlightsImplDetailsPanelBase prepareWidget() {
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

package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplCCSSCoverageChart extends HighlightsImplBase {
    HighlightsImplCCSSCoverageChartPanel panel = new HighlightsImplCCSSCoverageChartPanel(this);
    static String title = "CCSS Coverage Chart";
    
    public HighlightsImplCCSSCoverageChart() {
        super(title,"Displays the number of CCSS standards covered over time.");
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

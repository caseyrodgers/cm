package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplCCSSGroupCoverage extends HighlightsImplBase {
    HighlightsImplCCSSGroupCoveragePanel panel = new HighlightsImplCCSSGroupCoveragePanel(this);
    static String title = "CCSS Group Coverage";
    
    public HighlightsImplCCSSGroupCoverage() {
        super(title,"Displays CCSS standards and the number of students that have covered them orderd by student groups.");
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

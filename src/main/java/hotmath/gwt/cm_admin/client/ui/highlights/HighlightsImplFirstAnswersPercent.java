package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplFirstAnswersPercent extends HighlightsImplBase {
    HighlightsImplFirstAnswersPercentPanel panel = new HighlightsImplFirstAnswersPercentPanel(this);
    static String title = "First Answer Percent Correct";
    public HighlightsImplFirstAnswersPercent() {
        super(title,"Percent correct of first time answers.");
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

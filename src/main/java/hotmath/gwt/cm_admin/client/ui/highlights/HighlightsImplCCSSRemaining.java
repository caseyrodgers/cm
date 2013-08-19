package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

public class HighlightsImplCCSSRemaining extends HighlightsImplBase {
    HighlightsImplCCSSRemainingPanel panel = new HighlightsImplCCSSRemainingPanel(this);
    static String title = "CCSS Remaining";
    
    public HighlightsImplCCSSRemaining() {
        super(title,"Displays CCSS standards for the selected strand and the number of students that have <b>not</b> covered them.");
    }

    @Override
    public Widget prepareWidget() {
        return panel; //.getPanel();
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

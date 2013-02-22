package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplZeroLogins extends HighlightsImplBase {
    HighlightsImplZeroLoginsDetailsPanel panel = new HighlightsImplZeroLoginsDetailsPanel(this);
    static String title = "Zero Logins";
    public HighlightsImplZeroLogins() {
        super(title, "List students that did not log in during the date range.");
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
}

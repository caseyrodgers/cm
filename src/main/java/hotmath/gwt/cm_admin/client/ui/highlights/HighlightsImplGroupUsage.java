package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplGroupUsage extends HighlightsImplBase {
    HighlightsImplGroupUsageDetailsPanel panel = new HighlightsImplGroupUsageDetailsPanel(this);
    static String title = "Group Usage";
    public HighlightsImplGroupUsage() {
        super("Group Usage","Shows the usage of optional learning resources for groups with at least one active student.");
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

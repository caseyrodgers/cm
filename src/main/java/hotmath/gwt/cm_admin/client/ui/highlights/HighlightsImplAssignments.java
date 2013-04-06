package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplAssignments extends HighlightsImplBase {
    HighlightsImplAssignmentsPanel panel = new HighlightsImplAssignmentsPanel(this);
    static String title = "Graded Assignments";
    public HighlightsImplAssignments() {
        super(title, "Displays students with one or more graded assignments.");
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

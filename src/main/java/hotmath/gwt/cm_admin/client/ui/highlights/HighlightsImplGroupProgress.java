package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplGroupProgress extends HighlightsImplBase {
    HighlightsImplGroupProgressDetailsPanel panel = new HighlightsImplGroupProgressDetailsPanel(this);
    static String title = "Group Progress";
    public HighlightsImplGroupProgress() {
        super(title,"Shows number of active students (logged in at least once), total logins, lessons viewed, and quizzes passed for each group and entire school. Groups with no active students are omitted.");
    }
    public Widget prepareWidget() {
        return panel;
    }
    
    @Override
    public String[][] getReportValues() {
        return panel.getReportValues();
    }
    
    @Override
    String[] getReportCols() {
        return panel.getReportColumns();
    }
}

package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

public class HighlightsImplMostFailuresLatestQuiz extends HighlightsImplBase {
    HighlightsImplMostFailuresLatestQuizDetailsPanel panel = new HighlightsImplMostFailuresLatestQuizDetailsPanel(this);
    static String title = "Most Failures of Current Quiz";
    public HighlightsImplMostFailuresLatestQuiz() {
        super(title,"Displays students who have failed their current quiz at least once, in rank order - a possible indicator of needing teacher assistance.");
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

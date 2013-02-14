package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

public class HighlightsImplMostFailuresLatestQuiz extends HighlightsImplBase {
    HighlightsImplMostFailuresLatestQuizDetailsPanel panel = new HighlightsImplMostFailuresLatestQuizDetailsPanel(this);
    static String title = "Most Failures of Current Quiz";
    public HighlightsImplMostFailuresLatestQuiz() {
        super(title,"Displays students who have failed their current quiz at least once, in rank order - a possible indicator of needing teacher assistance.");
    }
    public Widget prepareWidget() {
        return panel; 
    }
    /*
    @Override
    protected String[][] getReportValues() {
        return panel.getReportValues();
    }
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:75", "Failures:25"};
        HighlightReportLayout rl = new HighlightReportLayout(title, "Student Count: ", cols, panel.getReportValues());
        return rl;
    }
    */
}

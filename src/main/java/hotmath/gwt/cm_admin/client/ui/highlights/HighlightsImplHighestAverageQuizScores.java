package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

public class HighlightsImplHighestAverageQuizScores extends HighlightsImplBase {
    HighlightsImplHighestAverageQuizScoresDetailsPanel panel = new HighlightsImplHighestAverageQuizScoresDetailsPanel(this);
    static String title = "Highest Average Quiz Score";
    
    public HighlightsImplHighestAverageQuizScores() {
        super(title,"Displays students in order of their average quiz score, including passed and failed quizzes, but excluding Auto-Enroll quizzes.");
    }
    public Widget prepareWidget() {
        return panel;
    }
/*    
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:50", "Average:20", "Quizzes Taken:20"};
        HighlightReportLayout rl = new HighlightReportLayout(title, "Student Count: ", cols, panel.getReportValues());
        return rl;
    }
*/
}

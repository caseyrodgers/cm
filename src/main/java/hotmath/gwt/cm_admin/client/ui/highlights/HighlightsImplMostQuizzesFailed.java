package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

public class HighlightsImplMostQuizzesFailed extends HighlightsImplBase {
    HighlightsImplMostQuizzesFailedDetailsPanel panel = new HighlightsImplMostQuizzesFailedDetailsPanel(this);
    static String title = "Most Quizzes Failed";
    public HighlightsImplMostQuizzesFailed() {
        super(title,"Displays students in order of most quizzes failed, a possible indicator of students in a program above their level.");
    }
    public Widget prepareWidget() {
        return panel;
    }
/*
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:75", "Failures:25"};
        HighlightReportLayout rl = new HighlightReportLayout(title, "Student Count: ", cols, panel.getReportValues());
        return rl;
    }
    */
}

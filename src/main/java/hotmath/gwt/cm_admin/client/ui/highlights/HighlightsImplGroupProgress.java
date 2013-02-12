package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

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
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Group:50", "Active:12", "Logins:12", "Lessons:12", "Passed:12"};
        HighlightReportLayout rl = new HighlightReportLayout(title, "Group Count: ", cols, panel.getReportValues());
        return rl;
    }

}

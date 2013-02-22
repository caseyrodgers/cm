package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

public class HighlightsImplFirstAnswersPercent extends HighlightsImplBase {
    HighlightsImplFirstAnswersPercentPanel panel = new HighlightsImplFirstAnswersPercentPanel(this);
    static String title = "First Answer Percent Correct";
    public HighlightsImplFirstAnswersPercent() {
        super(title,"Percent correct of first time answers.");
    }
    public Widget prepareWidget() {
        return panel;
    }
/*
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:75", "Time-on-Task:25"};
        HighlightReportLayout rl = new HighlightReportLayout(cols, panel.getReportValues());
        rl.setTitle(title);
        return rl;
    }
*/
}

package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

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
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:100"};
        HighlightReportLayout rl = new HighlightReportLayout(cols, panel.getReportValues());
        rl.setTitle(title);
        return rl;
    }
}

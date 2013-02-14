package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

public class HighlightsImplGroupUsage extends HighlightsImplBase {
    HighlightsImplGroupUsageDetailsPanel panel = new HighlightsImplGroupUsageDetailsPanel(this);
    static String title = "Group Usage";
    public HighlightsImplGroupUsage() {
        super("Group Usage","Shows the usage of optional learning resources for groups with at least one active student.");
    }
    public Widget prepareWidget() {
        return panel;
    }
    /*
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Group:50", "Active:12", "Videos:12", "Games:12", "Activities:12"};
        HighlightReportLayout rl = new HighlightReportLayout(title, "Group Count: ", cols, panel.getReportValues());
        return rl;
    }
    */

}

package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;

public class HighlightsImplTimeOnTask extends HighlightsImplBase {
    HighlightsImplTimeOnTaskPanel panel = new HighlightsImplTimeOnTaskPanel(this);
    static String title = "Time-on-Task";
    public HighlightsImplTimeOnTask() {
        super(title,"Estimated time-on-task");
    }
    public Widget prepareWidget() {
        return panel;
    }
    @Override
    protected HighlightReportLayout getReportLayout() {
        String cols[] = {"Name:75", "Time-on-Task:25"};
        HighlightReportLayout rl = new HighlightReportLayout(cols, panel.getReportValues());
        rl.setTitle(title);
        return rl;
    }

}

package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

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
    String[] getReportCols() {
        return panel.getReportColumns();
    }
    
    @Override
    String[][] getReportValues() {
        return panel.getReportValues();
    }
}

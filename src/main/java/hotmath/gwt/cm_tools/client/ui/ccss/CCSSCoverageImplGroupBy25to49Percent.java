package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

public class CCSSCoverageImplGroupBy25to49Percent extends CCSSCoverageImplBase {

    CCSSCoverageImplGroupBy25to49PercentPanel panel;

    static String title = "25 to 49 percent of Students";

    public CCSSCoverageImplGroupBy25to49Percent(int groupId, int adminId, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage by 25 to 49 percent of students in group.", groupId, callback);
        panel = new CCSSCoverageImplGroupBy25to49PercentPanel(this, groupId, adminId);
    }
    
    public Widget prepareWidget() {
        return panel;
    }

    @Override
    protected String[][] getReportValues() {
        return panel.getPanelValues();
    }
    
    @Override
    String[] getReportCols() {
        return panel.getPanelColumns();
    }
}

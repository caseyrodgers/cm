package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

public class CCSSCoverageImplGroupBy50to74Percent extends CCSSCoverageImplBase {

    CCSSCoverageImplGroupBy50to74PercentPanel panel;

    static String title = "50 to 74 percent of Students";

    public CCSSCoverageImplGroupBy50to74Percent(int groupId, int adminId, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage by 50 to 74 percent of students in group.", groupId, callback);
        panel = new CCSSCoverageImplGroupBy50to74PercentPanel(this, groupId, adminId);
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

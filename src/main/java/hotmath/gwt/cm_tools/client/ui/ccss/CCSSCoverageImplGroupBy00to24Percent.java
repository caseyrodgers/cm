package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

public class CCSSCoverageImplGroupBy00to24Percent extends CCSSCoverageImplBase {

    CCSSCoverageImplGroupBy00to24PercentPanel panel;

    static String title = "Less than 25 percent of Students";

    public CCSSCoverageImplGroupBy00to24Percent(int groupId, int adminId, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage by less than 25 percent of students in group.", groupId, callback);
        panel = new CCSSCoverageImplGroupBy00to24PercentPanel(this, groupId, adminId);
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

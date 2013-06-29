package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

public class CCSSCoverageImplGroupBy00to49Percent extends CCSSCoverageImplBase {

    CCSSCoverageImplGroupBy00to49PercentPanel panel;

    static String title = "Standards covered by some students";

    public CCSSCoverageImplGroupBy00to49Percent(int groupId, int adminId, CallbackOnComplete callback) {
        super(title, "Displays standards covered by some students (less than 50 percent).", groupId, callback);
        panel = new CCSSCoverageImplGroupBy00to49PercentPanel(this, groupId, adminId);
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

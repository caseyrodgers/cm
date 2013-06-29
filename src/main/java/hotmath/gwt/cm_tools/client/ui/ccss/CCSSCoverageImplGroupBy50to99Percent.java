package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

public class CCSSCoverageImplGroupBy50to99Percent extends CCSSCoverageImplBase {

    CCSSCoverageImplGroupBy50to99PercentPanel panel;

    static String title = "Standards covered by most students";

    public CCSSCoverageImplGroupBy50to99Percent(int groupId, int adminId, CallbackOnComplete callback) {
        super(title, "Displays standards covered by most students (more than 50%).", groupId, callback);
        panel = new CCSSCoverageImplGroupBy50to99PercentPanel(this, groupId, adminId);
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

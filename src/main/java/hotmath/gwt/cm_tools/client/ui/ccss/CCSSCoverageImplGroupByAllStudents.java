package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

public class CCSSCoverageImplGroupByAllStudents extends CCSSCoverageImplBase {

    CCSSCoverageImplGroupByAllStudentsPanel panel;

    static String title = "All Students";

    public CCSSCoverageImplGroupByAllStudents(int groupId, int adminId, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage by all students in group.", groupId, callback);
        panel = new CCSSCoverageImplGroupByAllStudentsPanel(this, groupId, adminId);
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

package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplAssignment extends CCSSCoverageImplBase {
    static String title = "Assignment";
    CCSSCoverageImplAssignmentPanel panel;
    public CCSSCoverageImplAssignment(int assignKey, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage for an Assignment.", assignKey, callback);
        panel = new CCSSCoverageImplAssignmentPanel(this, assignKey);
    }

    public Widget prepareWidget() {
        return panel;
    }
    
    @Override
    String[] getReportCols() {
        return panel.getPanelColumns();
    }
    
    @Override
    String[][] getReportValues() {
        return panel.getPanelValues();
    }  
}
package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplStudentAssignedCompleted extends CCSSCoverageImplBase {
    CCSSCoverageImplStudentAssignedCompletedPanel panel;
    static String title = "Completed Assignments";
    public CCSSCoverageImplStudentAssignedCompleted(int userId, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage for completed assignments.", userId, callback);
        panel = new CCSSCoverageImplStudentAssignedCompletedPanel(this, userId);
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
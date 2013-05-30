package hotmath.gwt.cm_tools.client.ui.ccss;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplStudentAssignedCompleted extends CCSSCoverageImplBase {
    CCSSCoverageImplStudentAssignedCompletedPanel panel;
    static String title = "Completed Assignments";
    public CCSSCoverageImplStudentAssignedCompleted(int userId) {
        super(title, "Displays CCSS coverage for completed assignments.", userId);
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
package hotmath.gwt.cm_tools.client.ui.ccss;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplStudentAssignedCompleted extends CCSSCoverageImplBase {
    CCSSCoverageImplStudentAssignedCompletedPanel panel = new CCSSCoverageImplStudentAssignedCompletedPanel(this);
    static String title = "Completed Assignments";
    public CCSSCoverageImplStudentAssignedCompleted(int userId) {
        super(title, "Displays CCSS coverage for student's completed assignments.", userId);
        panel.setUserId(userId);
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
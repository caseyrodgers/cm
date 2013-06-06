package hotmath.gwt.cm_tools.client.ui.ccss;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplStudentCombined extends CCSSCoverageImplBase {
    CCSSCoverageImplStudentCombinedPanel panel;
    static String title = "All Combined";
    public CCSSCoverageImplStudentCombined(int userId) {
        super(title, "Displays combined CCSS coverage for correct quiz answers, reviewed lessons, and completed assignments.", userId);
        panel = new CCSSCoverageImplStudentCombinedPanel(this, userId);
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
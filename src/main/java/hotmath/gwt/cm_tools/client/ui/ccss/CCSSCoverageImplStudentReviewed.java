package hotmath.gwt.cm_tools.client.ui.ccss;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplStudentReviewed extends CCSSCoverageImplBase {
    CCSSCoverageImplStudentReviewedPanel panel = new CCSSCoverageImplStudentReviewedPanel(this);
    static String title = "Reviewed Lessons";
    public CCSSCoverageImplStudentReviewed(int userId) {
        super(title, "Displays CCSS coverage for student's reviewed lessons.", userId);
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
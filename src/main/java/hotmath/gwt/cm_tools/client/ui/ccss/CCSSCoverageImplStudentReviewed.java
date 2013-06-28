package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplStudentReviewed extends CCSSCoverageImplBase {
    CCSSCoverageImplStudentReviewedPanel panel;
    static String title = "Reviewed Lessons";
    public CCSSCoverageImplStudentReviewed(int userId, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage for reviewed lessons.", userId, callback);
        panel = new CCSSCoverageImplStudentReviewedPanel(this, userId);
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
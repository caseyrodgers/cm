package hotmath.gwt.cm_tools.client.ui.ccss;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplStudentQuizzedPassed extends CCSSCoverageImplBase {
    CCSSCoverageImplStudentQuizzedPassedPanel panel = new CCSSCoverageImplStudentQuizzedPassedPanel(this);
    static String title = "Quizzed and Passed";
    public CCSSCoverageImplStudentQuizzedPassed(int userId) {
        super(title, "Displays CCSS coverage for student's correct quiz answers.", userId);
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
package hotmath.gwt.cm_tools.client.ui.ccss;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplStudentQuizzedPassed extends CCSSCoverageImplBase {
    static String title = "Quizzed and Passed";
    CCSSCoverageImplStudentQuizzedPassedPanel panel;
    public CCSSCoverageImplStudentQuizzedPassed(int userId) {
        super(title, "Displays CCSS coverage for student's correct quiz answers.", userId);
        panel = new CCSSCoverageImplStudentQuizzedPassedPanel(this, userId);
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
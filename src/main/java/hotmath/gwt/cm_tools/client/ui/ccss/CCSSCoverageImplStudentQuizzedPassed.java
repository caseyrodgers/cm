package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplStudentQuizzedPassed extends CCSSCoverageImplBase {
    static String title = "Quizzed and Passed";
    CCSSCoverageImplStudentQuizzedPassedPanel panel;
    public CCSSCoverageImplStudentQuizzedPassed(int userId, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage for correct quiz answers.", userId, callback);
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
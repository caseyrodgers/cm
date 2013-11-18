package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.user.client.ui.Widget;

class CCSSCoverageImplCustomQuiz extends CCSSCoverageImplBase {
    static String title = "Custom Quiz";
    CCSSCoverageImplCustomQuizPanel panel;
    public CCSSCoverageImplCustomQuiz(int customQuizId, CallbackOnComplete callback) {
        super(title, "Displays CCSS coverage for a Custom Quiz.", customQuizId, callback);
        panel = new CCSSCoverageImplCustomQuizPanel(this, customQuizId);
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